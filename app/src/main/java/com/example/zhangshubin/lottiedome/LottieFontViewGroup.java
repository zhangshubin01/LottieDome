package com.example.zhangshubin.lottiedome;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LottieFontViewGroup extends FrameLayout {
  private final Map<String, LottieComposition> compositionMap = new HashMap<>();
  private final List<View> views = new ArrayList<>();

  @Nullable private LottieAnimationView cursorView;

  public LottieFontViewGroup(Context context) {
    super(context);
    init();
  }

  public LottieFontViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LottieFontViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
   setFocusableInTouchMode(true); //获取焦点 软键盘输入
    //加载 Mobilo/BlinkingCursor.json  的动画效果
    LottieComposition.Factory.fromAssetFileName(getContext(), "Mobilo/BlinkingCursor.json",
        new OnCompositionLoadedListener() {
          @Override
          public void onCompositionLoaded(LottieComposition composition) {
            cursorView = new LottieAnimationView(getContext());
            cursorView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cursorView.setComposition(composition);
            cursorView.loop(true);
            cursorView.playAnimation();
            addView(cursorView);
          }
        });
  }

  private void addSpace() {
    int index = indexOfChild(cursorView);  //得到在容器中的下标  没有此视图 返回-1
    addView(createSpaceView(), index);
  }

  @Override
  public void addView(View child, int index) {
    super.addView(child, index);
    if (index == -1) {
      views.add(child);
    } else {
      views.add(index, child);
    }
  }

  private void removeLastView() {
    if (views.size() > 1) {
      int position = views.size() - 2;
      removeView(views.get(position));
      views.remove(position);
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    if (views.isEmpty()) {
      return;
    }
    int currentX = getPaddingTop();
    int currentY = getPaddingLeft();

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      if (!fitsOnCurrentLine(currentX, view)) {
        if (view.getTag() != null && view.getTag().equals("Space")) {
          continue;
        }
        currentX = getPaddingLeft();
        currentY += view.getMeasuredHeight();
      }
      currentX += view.getWidth();
    }

    setMeasuredDimension(getMeasuredWidth(),
        currentY + views.get(views.size() - 1).getMeasuredHeight() * 2);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (views.isEmpty()) {
      return;
    }
    int currentX = getPaddingTop();
    int currentY = getPaddingLeft();

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      if (!fitsOnCurrentLine(currentX, view)) {
        if (view.getTag() != null && view.getTag().equals("Space")) {
          continue;
        }
        currentX = getPaddingLeft();
        currentY += view.getMeasuredHeight();
      }
      view.layout(currentX, currentY, currentX + view.getMeasuredWidth(),
          currentY + view.getMeasuredHeight());
      currentX += view.getWidth();
    }
  }

  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    BaseInputConnection fic = new BaseInputConnection(this, false);
    outAttrs.actionLabel = null;
    outAttrs.inputType = InputType.TYPE_NULL;
    outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT;
    return fic;
  }

  @Override
  public boolean onCheckIsTextEditor() {
    return true;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {   //  监听键盘输入
    if (keyCode == KeyEvent.KEYCODE_SPACE) {  //当是空格键时
      addSpace();
      return true;
    }

    if (keyCode == KeyEvent.KEYCODE_DEL) { //	退格键
      removeLastView();
      return true;
    }

    if (!isValidKey(event)) {
      return super.onKeyUp(keyCode, event);
    }

    String letter = "" + Character.toUpperCase((char) event.getUnicodeChar());  //得到 大写的字母
    final String fileName = "Mobilo/" + letter + ".json";
    if (compositionMap.containsKey(fileName)) {  //判断是否有key 包含filename
      addComposition(compositionMap.get(fileName));
    } else {
      LottieComposition.Factory.fromAssetFileName(getContext(), fileName,  //如果没有
          new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
              compositionMap.put(fileName, composition);
              addComposition(composition);
            }
          });
    }

    return true;
  }

  private boolean isValidKey(KeyEvent event) {
    if (!event.hasNoModifiers()) {  //是否包含修饰键
      return false;
    }
    if (event.getKeyCode() >= KeyEvent.KEYCODE_A && event.getKeyCode() <= KeyEvent.KEYCODE_Z) {  //如果是A---Z 的
      return true;
    }
    return false;
  }
  private void addComposition(LottieComposition composition) {
    LottieAnimationView lottieAnimationView = new LottieAnimationView(getContext());
    lottieAnimationView.setLayoutParams(new LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ));
    lottieAnimationView.setComposition(composition);
    lottieAnimationView.playAnimation();
    if (cursorView == null) {   //如果没有cursorView 直接添加这个view
      addView(lottieAnimationView);
    } else {      //如果有  按照这个cursorView的坐标添加
      int index = indexOfChild(cursorView);
      addView(lottieAnimationView, index);
    }
  }

  private boolean fitsOnCurrentLine(int currentX, View view) {
    return currentX + view.getMeasuredWidth() < getWidth() - getPaddingRight();
  }

  /**
   * 创建一个view  设置12dp
   * @return
     */
  private View createSpaceView() {
    View spaceView = new View(getContext());
    spaceView.setLayoutParams(new LayoutParams(
        getResources().getDimensionPixelSize(R.dimen.font_space_width),
        ViewGroup.LayoutParams.WRAP_CONTENT
    ));
    spaceView.setTag("Space");   //view中缓存“space”
    return spaceView;
  }
}
