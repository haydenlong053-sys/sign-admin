/**
 * 百分比转换工具函数
 * 用于表单提交前将百分比数值转换为小数，以及页面加载时将小数转换为百分比显示
 */

/**
 * 将百分比字段转换为小数（提交时使用）
 * 规则：所有输入都视为百分比数值，统一除以100
 * 
 * 使用示例：
 * convertPercentageToDecimal(['field1', 'field2']);
 * 
 * @param {Array} percentageFields - 需要转换的字段名数组
 */
function convertPercentageToDecimal(percentageFields) {
    if (!percentageFields || !Array.isArray(percentageFields)) {
        console.warn('convertPercentageToDecimal: 请传入字段名数组');
        return;
    }
    
    percentageFields.forEach(function(fieldName) {
        var input = $('input[name="' + fieldName + '"]');
        var value = input.val();
        if (value && value !== '') {
            var numValue = parseFloat(value);
            if (!isNaN(numValue)) {
                // 所有输入都视为百分比数值，统一除以100
                // 例如：50 → 0.5, 5 → 0.05, 0.5 → 0.005
                input.val((numValue / 100).toFixed(8));
            }
        }
    });
}

/**
 * 将小数字段转换为百分比显示（页面回显时使用）
 * 规则：将所有小数值乘以100，转换为百分比形式显示
 * 
 * 使用示例：
 * convertDecimalToPercentage(['field1', 'field2']);
 * 
 * @param {Array} percentageFields - 需要转换的字段名数组
 */
function convertDecimalToPercentage(percentageFields) {
    if (!percentageFields || !Array.isArray(percentageFields)) {
        console.warn('convertDecimalToPercentage: 请传入字段名数组');
        return;
    }
    
    percentageFields.forEach(function(fieldName) {
        var input = $('input[name="' + fieldName + '"]');
        var value = input.val();
        if (value && value !== '') {
            var numValue = parseFloat(value);
            if (!isNaN(numValue)) {
                // 将小数转换为百分比（×100），保留2位小数
                input.val((numValue * 100).toFixed(2));
            }
        }
    });
}
