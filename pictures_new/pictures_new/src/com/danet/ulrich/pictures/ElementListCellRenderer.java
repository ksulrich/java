/**
 * $Id: ElementListCellRenderer.java,v 1.1.1.1 2008/03/21 13:38:38 klaus Exp $
 */
package com.danet.ulrich.pictures;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ElementListCellRenderer extends DefaultListCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8805380819711958561L;

	public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Element element = (Element) value;
        if (isSelected) {
            if (element.isMark()) {
                setBackground(Color.red);
            } else {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
        } else {
            if (element.isMark()) {
                setBackground(Color.yellow);
            } else {
                setBackground(list.getBackground());
            }
            setForeground(list.getForeground());
        }
        return this;
    }
}
