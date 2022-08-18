package com.applozic.mobicomkit.uiwidgets.customization;

public class DateFormatCustomization {
    public static final String SAME_DAY_TIME_TEMPLATE = "sameDayTimeTemplate";
    public static final String OTHER_DAY_DATE_TEMPLATE = "otherDayDateTemplate";
    public static final String TIME_TEMPLATE = "timeTemplate";
    public static final String TIME_AND_DATE_TEMPLATE = "timeAndDateTemplate";

    private String sameDayTimeTemplate;
    private String otherDayDateTemplate;
    private String timeTemplate;
    private String timeAndDateTemplate;

    public String getSameDayTimeTemplate() {
        return sameDayTimeTemplate;
    }

    public void setSameDayTimeTemplate(String sameDayTimeTemplate) {
        this.sameDayTimeTemplate = sameDayTimeTemplate;
    }

    public String getOtherDayDateTemplate() {
        return otherDayDateTemplate;
    }

    public void setOtherDayDateTemplate(String otherDayDateTemplate) {
        this.otherDayDateTemplate = otherDayDateTemplate;
    }

    public String getTimeTemplate() {
        return timeTemplate;
    }

    public void setTimeTemplate(String timeTemplate) {
        this.timeTemplate = timeTemplate;
    }

    public String getTimeAndDateTemplate() {
        return timeAndDateTemplate;
    }

    public void setTimeAndDateTemplate(String dateAndTimeTemplate) {
        this.timeAndDateTemplate = dateAndTimeTemplate;
    }
}
