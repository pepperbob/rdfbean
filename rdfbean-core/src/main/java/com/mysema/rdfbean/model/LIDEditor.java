/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.rdfbean.model;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;

/**
 * @author sasa
 *
 */
public class LIDEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        LID uid = (LID) getValue();
        if (uid == null) {
            return "";
        } else {
            return uid.getId();
        }
    }

    @Override
    public void setAsText(String text) {
        if (StringUtils.isEmpty(text)) {
            setValue(null);
        } else {
            setValue(new LID(text));
        }
    }

}
