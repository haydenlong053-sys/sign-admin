/**
 * BigDecimal科学计数法修复工具
 * 用于将科学计数法格式转换为普通数字格式
 */

/**
 * 修复表单中BigDecimal字段的科学计数法显示
 * @param {Array} fields - 需要修复的字段名数组
 */
function fixBigDecimalDisplay(fields) {
    if (!fields || !Array.isArray(fields)) {
        return;
    }
    
    fields.forEach(function(fieldName) {
        var input = $('input[name="' + fieldName + '"]');
        if (input.length > 0) {
            var value = input.val();
            // 检查是否包含科学计数法（E或e）
            if (value && (value.indexOf('E') !== -1 || value.indexOf('e') !== -1)) {
                // 使用parseFloat转换，然后toFixed保留8位小数
                var numValue = parseFloat(value);
                if (!isNaN(numValue)) {
                    // 去除末尾的零
                    var formatted = numValue.toFixed(8).replace(/\.?0+$/, '');
                    input.val(formatted);
                }
            }
        }
    });
}
