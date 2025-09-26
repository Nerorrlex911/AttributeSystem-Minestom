function format(num, pattern) {
    // pattern形如 '#.##'，提取小数位数
    const match = pattern.match(/#\.(#+)/);
    const decimals = match ? match[1].length : 0;
    return Number(num).toFixed(decimals);
}

function physical_attack_holo(data) {
    // 解析变量
    let result = this.result;
    let crit = this.crit;
    let vampire = this.vampire;
    // 计算 common
    let common = result > 0.0 ? data.handle(`&6${format(result, '#.##')}`) : '&7&lMISS';
    // 计算 crit
    crit = crit > 0.0 ? '&4✵' : '';
    // 计算 vampire
    vampire = vampire > 0.0 ? data.handle(`&a+${format(vampire, '#.##')}`) : '';
    // 拼接结果
    let output = [crit,common,vampire].join('');
    print("output");
    print(output);
    return output;
}

function physical_attack_chat(data) {
    let result = this.result;
    let crit = this.crit;
    let vampire = this.vampire;
    let prefix = `&d${this.name}&5: `;
    let common = result > 0.0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    crit = crit > 0.0 ? '&4✵' : '';
    vampire = vampire > 0.0 ? `&a+${format(vampire, '#.##')}` : '';
    return [prefix, crit, common, vampire].join('');
}

function physical_attack_subtitle(data) {
    let result = this.result;
    let vampire = this.vampire;
    let crit = this.crit;
    crit = crit > 0.0 ? '&4暴击' : '&4';
    let common = result > 0.0 ? `${crit}                &c${format(result, '#.##')}` : '&7&lMISS';
    vampire = vampire > 0.0 ? `&a+${format(vampire, '#.##')}` : '';
    return ['!', common, vampire].join('');
}

function physical_attack_actionbar(data) {
    let result = this.result;
    let crit = this.crit;
    let vampire = this.vampire;
    let prefix = `&d${this.name}&5: `;
    let common = result > 0.0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    crit = crit > 0.0 ? '&4✵' : '';
    vampire = vampire > 0.0 ? `&c吸血&a${format(vampire, '#.##')}` : '';
    return [prefix, crit, common, vampire].join('');
}

// defend
function physical_defend_holo(data) {
    let result = this.result;
    let crit = this.crit;
    let subtract = '&c- ';
    let common = result !== 0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    crit = crit > 0.0 ? '&4✵' : '';
    return [subtract, crit, common].join('');
}

function physical_defend_chat(data) {
    let result = this.result;
    let crit = this.crit;
    let prefix = `&d${this.name}&5: `;
    let common = result !== 0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    crit = crit > 0.0 ? '&4✵' : '';
    return [prefix, crit, common].join('');
}

function physical_defend_title(data) {
    let crit = this.crit;
    return crit !== 0 ? '&4受到暴击' : '&4';
}

function physical_defend_subtitle(data) {
    let result = this.result;
    let vampire = this.vampire;
    let crit = this.crit > 0.0 ? '&c-&4✵ ' : '&c- ';
    let common = result > 0.0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    return ['!', crit, common].join('');
}

function physical_defend_actionbar(data) {
    let result = this.result;
    let crit = this.crit;
    let vampire = this.vampire;
    let prefix = `&d${this.name}&5: `;
    let common = result > 0.0 ? `&6${format(result, '#.##')}` : '&7&lMISS';
    crit = crit > 0.0 ? '&4✵' : '';
    return [prefix, crit, common].join('');
}
