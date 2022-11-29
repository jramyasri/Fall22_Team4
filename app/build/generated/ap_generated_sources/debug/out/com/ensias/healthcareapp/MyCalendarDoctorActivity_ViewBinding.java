// Generated code from Butter Knife. Do not modify!
package com.ensias.healthcareapp;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MyCalendarDoctorActivity_ViewBinding implements Unbinder {
  private MyCalendarDoctorActivity target;

  @UiThread
  public MyCalendarDoctorActivity_ViewBinding(MyCalendarDoctorActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MyCalendarDoctorActivity_ViewBinding(MyCalendarDoctorActivity target, View source) {
    this.target = target;

    target.recycler_time_slot = Utils.findRequiredViewAsType(source, R.id.recycle_time_slot2, "field 'recycler_time_slot'", RecyclerView.class);
    target.calendarView = Utils.findRequiredViewAsType(source, R.id.calendarView2, "field 'calendarView'", HorizontalCalendarView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MyCalendarDoctorActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_time_slot = null;
    target.calendarView = null;
  }
}
