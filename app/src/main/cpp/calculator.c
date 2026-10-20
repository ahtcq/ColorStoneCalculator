#include <jni.h>
#include <math.h>
#include <stdio.h>

JNIEXPORT jstring JNICALL
Java_com_example_colorstone_MainActivity_nativeCalculate(
    JNIEnv *env, jobject thiz,
    jdouble length, jdouble width,
    jdouble slope, jdouble waste,
    jdouble tilesPerM2, jdouble pricePerTile,
    jdouble accessoryCost, jdouble laborCost) {

    (void)thiz;

    if (!isfinite(length) || !isfinite(width) ||
        !isfinite(slope) || !isfinite(waste) ||
        !isfinite(tilesPerM2) || !isfinite(pricePerTile) ||
        !isfinite(accessoryCost) || !isfinite(laborCost) ||
        length <= 0 || width <= 0 || slope < 1.0 ||
        waste < 0 || waste > 100 ||
        tilesPerM2 <= 0 || pricePerTile < 0 ||
        accessoryCost < 0 || laborCost < 0) {
        return (*env)->NewStringUTF(
            env, "输入数据无效，请检查各项数值。");
    }

    double area = length * width * slope;
    double materialArea = area * (1.0 + waste / 100.0);
    double tileCount = ceil(materialArea * tilesPerM2);
    double tileCost = tileCount * pricePerTile;
    double totalCost = tileCost + accessoryCost + laborCost;

    if (!isfinite(area) || !isfinite(materialArea) ||
        !isfinite(tileCount) || !isfinite(totalCost)) {
        return (*env)->NewStringUTF(
            env, "数值超出计算范围，请检查输入。");
    }

    char result[1024];

    snprintf(result, sizeof(result),
        "===== 彩石瓦工程报价 =====\n\n"
        "实际屋面面积：%.2f 平方米\n"
        "含损耗计算面积：%.2f 平方米\n"
        "预计瓦片数量：%.0f 片\n\n"
        "瓦片材料费：%.2f 元\n"
        "配件费用：%.2f 元\n"
        "人工费用：%.2f 元\n"
        "------------------------\n"
        "预计总费用：%.2f 元\n\n"
        "说明：此结果为估算，实际用量请以厂家规格及现场测量为准。",
        area, materialArea, tileCount,
        tileCost, accessoryCost, laborCost, totalCost);

    return (*env)->NewStringUTF(env, result);
}