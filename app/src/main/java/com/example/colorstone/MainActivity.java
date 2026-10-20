
package com.example.colorstone;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("calculator");
    }

    private EditText length, width, slope, waste;
    private EditText tiles, price, accessories, labor;
    private TextView result;

    private native String nativeCalculate(
        double length, double width, double slope,
        double waste, double tiles, double price,
        double accessories, double labor
    );

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(32, 24, 32, 32);
        page.setBackgroundColor(Color.rgb(246, 247, 249));
        scroll.addView(page);

        TextView title = new TextView(this);
        title.setText("彩石瓦工程报价");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.rgb(37, 57, 74));
        page.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("屋面面积 · 瓦片用量 · 工程费用");
        subtitle.setTextSize(14);
        page.addView(subtitle);

        length = field(page, "屋面长度（米）", "10");
        width = field(page, "屋面水平宽度（米）", "5");
        slope = field(page, "坡度系数", "1.10");
        waste = field(page, "损耗率（%）", "5");
        tiles = field(page, "每平方米用瓦量（片）", "2.5");
        price = field(page, "每片瓦单价（元）", "3");
        accessories = field(page, "配件费用（元）", "0");
        labor = field(page, "人工费用（元）", "0");

        Button calculate = new Button(this);
        calculate.setText("计算工程报价");
        page.addView(calculate, params());
        calculate.setOnClickListener(v -> calculate());

        Button reset = new Button(this);
        reset.setText("恢复示例数据");
        page.addView(reset, params());
        reset.setOnClickListener(v -> reset());

        result = new TextView(this);
        result.setTextSize(16);
        result.setTextColor(Color.rgb(36, 48, 60));
        result.setPadding(24, 24, 24, 24);
        result.setBackgroundColor(Color.WHITE);
        result.setText("填写参数后，点击计算工程报价。");
        LinearLayout.LayoutParams rp = params();
        rp.topMargin = 16;
        page.addView(result, rp);

        setContentView(scroll);
    }

    private LinearLayout.LayoutParams params() {
        return new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private EditText field(LinearLayout page,
                           String label, String value) {
        TextView caption = new TextView(this);
        caption.setText(label);
        caption.setTextSize(14);
        LinearLayout.LayoutParams cp = params();
        cp.topMargin = 12;
        page.addView(caption, cp);

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(value);
        input.setTextSize(16);
        input.setSelectAllOnFocus(true);
        input.setInputType(8194);
        page.addView(input, params());
        return input;
    }

    private double number(EditText input) {
        String value = input.getText().toString().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("请填写所有项目。");
        }
        return Double.parseDouble(value);
    }

    private void calculate() {
        try {
            double l = number(length);
            double w = number(width);
            double s = number(slope);
            double wa = number(waste);
            double t = number(tiles);
            double p = number(price);
            double a = number(accessories);
            double laborCost = number(labor);

            if (l <= 0 || w <= 0 || s < 1 ||
                wa < 0 || wa > 100 || t <= 0 ||
                p < 0 || a < 0 || laborCost < 0) {
                throw new IllegalArgumentException(
                    "请检查长度、宽度、坡度、损耗和费用。");
            }

            result.setText(nativeCalculate(
                l, w, s, wa, t, p, a, laborCost));

            InputMethodManager imm =
                (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                result.getWindowToken(), 0);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的数字。",
                Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(),
                Toast.LENGTH_LONG).show();
        }
    }

    private void reset() {
        length.setText("10");
        width.setText("5");
        slope.setText("1.10");
        waste.setText("5");
        tiles.setText("2.5");
        price.setText("3");
        accessories.setText("0");
        labor.setText("0");
        result.setText("示例数据已恢复，请点击计算工程报价。");
    }
}
