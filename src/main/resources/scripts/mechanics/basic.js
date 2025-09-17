Plus = operation("Plus")
Scalar = operation("Scalar")
/*
set type = when of {type} {
case == 'PVP' -> '{a.as_att:PVPDamage} - {d.as_att:PVPDefense}'
case == 'PVE' -> '{a.as_att:PVEDamage} - {d.as_att:PVEDefense}'
else -> 0
}
set projectile to if check {projectile} == true then '{a.as_att:ProjectileDamage} - {d.as_att:ProjectileDefense}' else '0'
set damage to '{a.as_att:PhysicalDamage} + {origin}'
set defense to if check random 0 to 1 < {a.as_att:PhysicalDefenseIgnore} then 0 else '{d.as_att:PhysicalDefense} - {a.as_att:PhysicalPenetration}'
set force to if has force then {force} else 1
calculate '{&damage} + {$type} + {&projectile} - {&defense} ) * {&force}'
 */
function defaultGroup(data) {
    const aAttrData = AttrAPI.getAttrData(data.attacker);
    const dAttrData = AttrAPI.getAttrData(data.defender);
    let typeDamage = 0.0;
    let projectileDamage = 0.0;
    let defense = 0.0
    let force = 1.0;
    switch (this["type"]) {
        case "PVP":
            typeDamage = toDouble(aAttrData.getAttrValue("PVPDamage")) - toDouble(dAttrData.getAttrValue("PVPDefense"));
            break;
        case "PVE":
            typeDamage = toDouble(aAttrData.getAttrValue("PVEDamage")) - toDouble(dAttrData.getAttrValue("PVEDefense"));
            break;
        default:
            typeDamage = 0.0;
            break;
    }
    if (this["projectile"] === true) {
        projectileDamage = toDouble(aAttrData.getAttrValue("ProjectileDamage")) - toDouble(dAttrData.getAttrValue("ProjectileDefense"));
    } else {
        projectileDamage = 0.0;
    }
    if (Math.random() < toDouble(aAttrData.getAttrValue("PhysicalDefenseIgnore"))) {
        defense = 0.0;
    } else {
        defense = toDouble(dAttrData.getAttrValue("PhysicalDefense")) - toDouble(aAttrData.getAttrValue("PhysicalPenetration"));
    }
    if(data.containsKey("force")) {
        force = toDouble(data.get("force"));
    }
    const damage = (toDouble(aAttrData.getAttrValue("PhysicalDamage")) + toDouble(data.calResult()));
    return (damage + typeDamage + projectileDamage - defense)*force


}

//@Mechanic(damage)
function damage(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        data.hasResult = false;
        return 0.0;
    }
    const value = toDouble(data.handle(context.get("value")));
    data.damageSources.put("damage", Plus.element(value));
    //返回值会以 damage 为id 存到FightData里
    return value;
}

//@Mechanic(crit)
function crit(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const multiplier = toDouble(data.handle(context.get("multiplier")));
    data.damageSources.put("crit", Scalar.element(multiplier));
    //返回值会以 damage 为id 存到FightData里
    return multiplier;
}

//@Mechanic(vampire)
function vampire(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    if (attacker == null) return null;
    let healthRegain = toDouble(data.handle(context.get("value")));
    const maxHealth = attacker.maxHealth;
    const healthNow = attacker.health;
    const healthValue = healthNow + healthRegain;
    if (healthValue >= maxHealth) {
        attacker.health = maxHealth;
        healthRegain = maxHealth - healthNow;
    } else {
        attacker.health = healthValue;
    }
    return healthRegain;
}
function toDouble(obj) {
    if (obj === null || typeof obj === "undefined") {
        return 0.0;
    }
    if (typeof obj === "number" || obj instanceof Number) {
        return Number(obj);
    }
    var parsed = parseFloat(obj);
    if (!isNaN(parsed)) {
        return parsed;
    }
    return 0.0;
}