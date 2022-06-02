package com.ssu.takecare.AssistClass;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class CalendarDecorator implements DayViewDecorator {
    private CalendarDay date;
    public CalendarDecorator(){
        date=CalendarDay.today();
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //오늘 날짜에 효과주기
        view.addSpan(new ForegroundColorSpan(Color.GREEN));
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.4f));
    }
    public void setDate(Date date){
        this.date=CalendarDay.from(date);
    }
}
