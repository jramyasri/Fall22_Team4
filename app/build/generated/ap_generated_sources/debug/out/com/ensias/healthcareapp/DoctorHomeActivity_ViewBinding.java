// Generated code from Butter Knife. Do not modify!
package com.ensias.healthcareapp;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DoctorHomeActivity_ViewBinding implements Unbinder {
  private DoctorHomeActivity target;

  private View view7f0a0161;

  private View view7f0a0131;

  @UiThread
  public DoctorHomeActivity_ViewBinding(DoctorHomeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DoctorHomeActivity_ViewBinding(final DoctorHomeActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.profile, "method 'profileBtnClick'");
    view7f0a0161 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.profileBtnClick();
      }
    });
    view = Utils.findRequiredView(source, R.id.myCalendarBtn, "method 'myCalendarOnclick'");
    view7f0a0131 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.myCalendarOnclick();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    target = null;


    view7f0a0161.setOnClickListener(null);
    view7f0a0161 = null;
    view7f0a0131.setOnClickListener(null);
    view7f0a0131 = null;
  }
}
