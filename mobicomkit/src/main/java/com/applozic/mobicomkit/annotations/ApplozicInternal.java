package com.applozic.mobicomkit.annotations;

public @interface ApplozicInternal {

    /**
     * Used to denote what members of a class the annotation applies to.
     * This is to be used if the annotation is applied to a class.
     */
    AppliesTo[] appliesTo() default AppliesTo.SPECIFIED_MEMBERS;

    enum AppliesTo {
        SPECIFIED_MEMBERS, ALL_MEMBERS, STATIC_MEMBERS, INSTANCE_MEMBERS, STATIC_METHODS, INSTANCE_METHODS, STATIC_VARIABLES, INSTANCE_VARIABLES;
    }
}
