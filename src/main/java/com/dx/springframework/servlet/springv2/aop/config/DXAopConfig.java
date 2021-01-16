package com.dx.springframework.servlet.springv2.aop.config;


//import lombok.Data;


public class DXAopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspecAfter;
    private String aspecAfterThrow;
    private String aspecAfterThrowingName;

    public DXAopConfig() {
    }

    public DXAopConfig(String pointCut, String aspectClass, String aspectBefore, String aspecAfter, String aspecAfterThrow, String aspecAfterThrowingName) {
        this.pointCut = pointCut;
        this.aspectClass = aspectClass;
        this.aspectBefore = aspectBefore;
        this.aspecAfter = aspecAfter;
        this.aspecAfterThrow = aspecAfterThrow;
        this.aspecAfterThrowingName = aspecAfterThrowingName;
    }

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }

    public String getAspectClass() {
        return aspectClass;
    }

    public void setAspectClass(String aspectClass) {
        this.aspectClass = aspectClass;
    }

    public String getAspectBefore() {
        return aspectBefore;
    }

    public void setAspectBefore(String aspectBefore) {
        this.aspectBefore = aspectBefore;
    }

    public String getAspecAfter() {
        return aspecAfter;
    }

    public void setAspecAfter(String aspecAfter) {
        this.aspecAfter = aspecAfter;
    }

    public String getAspecAfterThrow() {
        return aspecAfterThrow;
    }

    public void setAspecAfterThrow(String aspecAfterThrow) {
        this.aspecAfterThrow = aspecAfterThrow;
    }

    public String getAspecAfterThrowingName() {
        return aspecAfterThrowingName;
    }

    public void setAspecAfterThrowingName(String aspecAfterThrowingName) {
        this.aspecAfterThrowingName = aspecAfterThrowingName;
    }

}
