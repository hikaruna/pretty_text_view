package ninja.hikaruna.prettytextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class PrettyTextView extends TextView {

    private int maxlines = 1;
    private boolean firstMeasure = false;
    private boolean widthUnspecified;
    private boolean heightUnspecified;

    public PrettyTextView(Context context) {
        super(context);
        init();
    }

    public PrettyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrettyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrettyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setMaxLines(maxlines);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthUnspecified = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED;
        heightUnspecified = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED;
        firstMeasure = true;

        resetTextSize(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        resetTextSize();
    }

    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
        this.maxlines = maxlines;
        resetTextSize();
    }

    private void resetTextSize() {
        resetTextSize(getWidth(), getHeight());
    }

    private void resetTextSize(int viewWidth, int viewHeight) {
        if (firstMeasure == false) {
            return;
        }
        if (widthUnspecified && heightUnspecified) {
            return;
        }

        int a = getCompoundPaddingTop();
        int b = getCompoundPaddingBottom();
        viewHeight = viewHeight - a - b;
        viewWidth = viewWidth - getCompoundPaddingLeft() - getCompoundPaddingRight();
        String text = getText().toString();
        if(text.isEmpty()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 0);
            return;
        }

        float textSize = 0;
        if (!widthUnspecified) {

            float textWidth;
            textWidth = calTextWidth(textSize, getText().toString());

            while (textWidth <= viewWidth) {
                textWidth = calTextWidth(++textSize, getText().toString());
            }
            textSize--;

        }

        if (!heightUnspecified) {
            float textHeight;
            textHeight = calTextHeight(textSize);

            while (textHeight > viewHeight) {
                textHeight = calTextHeight(--textSize);
            }


            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    private float calTextWidth(float textSize, String text) {
        // Paintにテキストサイズ設定
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setLinearText(false);

        // テキストの横幅取得
        return paint.measureText(text);
    }

    private float calTextHeight(float textSize) {
        // Paintにテキストサイズ設定
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setLinearText(false);

        // テキストの縦幅取
        Paint.FontMetrics fm = paint.getFontMetrics();
        float oneLineHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));
        return oneLineHeight * maxlines;
    }
}
