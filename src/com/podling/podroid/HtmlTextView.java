package com.podling.podroid;

import android.content.Context;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

// Based on http://stackoverflow.com/questions/8558732/listview-textview-with-linkmovementmethod-makes-list-item-unclickable
// TODO nicer solution.
public class HtmlTextView extends TextView {
	boolean dontConsumeNonUrlClicks = true;
	boolean linkHit;

	public HtmlTextView(Context context) {
		super(context);
	}

	public HtmlTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setHtml(String html) {
		setText(Html.fromHtml(html));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getMovementMethod() == null) {
			boolean result = super.onTouchEvent(event);
			return result;
		}

		MovementMethod m = getMovementMethod();
		setMovementMethod(null);

		boolean mt = m.onTouchEvent(this, (Spannable) getText(), event);
		if (mt && event.getAction() == MotionEvent.ACTION_DOWN) {
			event.setAction(MotionEvent.ACTION_UP);
			mt = m.onTouchEvent(this, (Spannable) getText(), event);
			event.setAction(MotionEvent.ACTION_DOWN);
		}

		boolean st = super.onTouchEvent(event);

		setMovementMethod(m);
		setFocusable(false);

		return mt || st;
	}

	public static class LocalLinkMovementMethod extends LinkMovementMethod {
		static LocalLinkMovementMethod sInstance;

		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();

			return sInstance;
		}

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer,
				MotionEvent event) {
			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}

					if (widget instanceof HtmlTextView) {
						((HtmlTextView) widget).linkHit = true;
					}
					return true;
				} else {
					Selection.removeSelection(buffer);
					Touch.onTouchEvent(widget, buffer, event);
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
}
