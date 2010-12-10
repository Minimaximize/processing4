/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2010 Ben Fry and Casey Reas

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2
  as published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package processing.mode.java;

import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import processing.app.Editor;
import processing.app.EditorToolbar;
import processing.app.EditorToolbar.Button;


public class Toolbar extends EditorToolbar {
  /** Rollover titles for each button. */
  static final String title[] = {
    "Run", "Stop", "New", "Open", "Save", "Export"
  };
  
  /** Titles for each button when the shift key is pressed. */ 
  static final String titleShift[] = {
    "Present", "Stop", "New Editor Window", "Open in Another Window", "Save", "Export to Application"
  };
  
  static final int RUN    = 0;
  static final int STOP   = 1;

  static final int NEW    = 2;
  static final int OPEN   = 3;
  static final int SAVE   = 4;
  static final int EXPORT = 5;

  JPopupMenu popup;
  JMenu menu;

  
  public Toolbar(Editor editor) {
    super(editor);
    
    Image[][] images = loadImages();
    for (int i = 0; i < 6; i++) {
      addButton(title[i], titleShift[i], images[i], i == NEW);
    }
  }


  public void handlePressed(MouseEvent e, int sel) {
    boolean shift = e.isShiftDown();
    JavaMode m = (JavaMode) mode;
    
    switch (sel) {
    case RUN:
      m.handleRun(shift);
      break;

    case STOP:
      m.handleStop();
      break;

    case OPEN:
      popup = menu.getPopupMenu();
      popup.show(this, e.getX(), e.getY());
      break;

    case NEW:
      if (shift) {
        mode.handleNew();
      } else {
        mode.handleNewReplace();
      }
      break;

    case SAVE:
      editor.handleSave(false);
      break;

    case EXPORT:
      if (shift) {
        m.handleExportApplication();
      } else {
        m.handleExport();
      }
      break;
    }
  }
}