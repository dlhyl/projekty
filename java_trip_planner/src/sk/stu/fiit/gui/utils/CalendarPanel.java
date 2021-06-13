/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stu.fiit.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Custom calendar using https://ateraimemo.com/Swing/CalendarViewList.html
 * @author dlhyl
 */
public class CalendarPanel extends RoundedPanel{
    public interface CalendarUpdate {
        public void updateCalendar(LocalDate value, CalendarPanel calendar);
    }
    
    public final Dimension size = new Dimension(40, 26);
    public final JLabel yearMonthLabel = new JLabel("", SwingConstants.CENTER);
    public final JList<LocalDate> monthList = new JList<LocalDate>() {};
    public final LocalDate realLocalDate = LocalDate.now();
    private LocalDate currentLocalDate;
    private LocalDate minDate = null;
    private Component c = null;

    public LocalDate getCurrentLocalDate() {
      return currentLocalDate;
    }

    public CalendarPanel() {
        super(30, new Color(0, 0, 0, 100));
        initCalendar();
    }

    public CalendarPanel(Component c, int cellWidth, int cellHeight) {
        super(30, new Color(0, 0, 0, 100));
        this.c = c;
        this.size.setSize(cellWidth, cellHeight);
        initCalendar();
    }

    private void initCalendar() {  
      setOpaque(false);
      monthList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      monthList.setVisibleRowCount(CalendarViewListModel.ROW_COUNT);
      monthList.setFixedCellWidth(size.width);
      monthList.setFixedCellHeight(size.height);
      monthList.setCellRenderer(new CalendarListRenderer());
      monthList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      installActions();
      Locale l = Locale.getDefault();
      DefaultListModel<DayOfWeek> weekModel = new DefaultListModel<>();
      DayOfWeek firstDayOfWeek = WeekFields.of(l).getFirstDayOfWeek();
      for (int i = 0; i < DayOfWeek.values().length; i++) {
        weekModel.add(i, firstDayOfWeek.plus(i));
      }
      JList<DayOfWeek> header = new JList<>(weekModel);
      header.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      header.setVisibleRowCount(1);
      header.setFixedCellWidth(size.width);
      header.setFixedCellHeight(size.height);
      header.setCellRenderer(new DefaultListCellRenderer() {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
          super.getListCellRendererComponent(list, value, index, false, false);
          setHorizontalAlignment(SwingConstants.CENTER);
          if (value instanceof DayOfWeek) {
            DayOfWeek dow = (DayOfWeek) value;
            // String s = dow.getDisplayName(TextStyle.SHORT_STANDALONE, l);
            // setText(s.substring(0, Math.min(2, s.length())));
            setText(dow.getDisplayName(TextStyle.SHORT_STANDALONE, l));
            setBackground(new Color(220, 220, 220));
          }
          return this;
        }
      });
      updateMonthView(realLocalDate);

      JButton prev = new JButton("<");
      prev.addActionListener(e -> updateMonthView(getCurrentLocalDate().minusMonths(1)));

      JButton next = new JButton(">");
      next.addActionListener(e -> updateMonthView(getCurrentLocalDate().plusMonths(1)));

      JButton vybrat = new JButton(java.util.ResourceBundle.getBundle("sk/stu/fiit/gui/bundle").getString("ZVOLIŤ"));
      vybrat.addActionListener(e -> {
          if (this.c != null) { 
              ((CalendarUpdate)c).updateCalendar(this.getSelectedValue(), this);
          }
      });
      vybrat.setAlignmentX(0.5f);

      JPanel yearMonthPanel = new JPanel(new BorderLayout());
      yearMonthPanel.setOpaque(false);
      yearMonthPanel.add(yearMonthLabel);
      yearMonthPanel.add(prev, BorderLayout.WEST);
      yearMonthPanel.add(next, BorderLayout.EAST);

      JScrollPane scroll = new JScrollPane(monthList);
      scroll.setColumnHeaderView(header);
      scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      yearMonthLabel.setFont(new java.awt.Font("Gill Sans MT", 1, 15)); // NOI18N
      yearMonthLabel.setForeground(new java.awt.Color(212, 175, 55));
      prev.setBackground(new Color(144,30,57));
      prev.setForeground(new Color(255,204,204));
      next.setBackground(new Color(144,30,57));
      vybrat.setBackground(new Color(144,30,57));
      next.setForeground(new Color(255,204,204));
      vybrat.setForeground(new Color(255,204,204));
      vybrat.setFont(new java.awt.Font("Gill Sans MT", 1, 14));

      Box box = Box.createVerticalBox();
      box.setOpaque(false);
      box.add(yearMonthPanel);
      box.add(Box.createVerticalStrut(2));
      box.add(scroll);
      box.add(Box.createVerticalStrut(5));
      box.add(vybrat);
      box.setBorder(new EmptyBorder(5,10,5,10));
      add(box, BorderLayout.CENTER);

    }

    public void setMinDate(LocalDate d) {
        this.minDate = d;
        if (!YearMonth.from(minDate).equals(YearMonth.from(currentLocalDate))) updateMonthView(minDate);
    }

    public LocalDate getSelectedValue() {
      LocalDate chosenDate = monthList.getSelectedValue();
      if (chosenDate != null && !(chosenDate.isBefore(realLocalDate) || (minDate != null && chosenDate.compareTo(minDate)<0)))
          return chosenDate;
      return null;
    }

    public void setSelectedValue(LocalDate d) {
        updateMonthView(d);
        monthList.setSelectedValue(d, false);
    }

      private void installActions() {
        InputMap im = monthList.getInputMap(JComponent.WHEN_FOCUSED);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "selectNextIndex");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "selectPreviousIndex");

        ActionMap am = monthList.getActionMap();
        am.put("selectPreviousIndex", new AbstractAction() {
          @Override public void actionPerformed(ActionEvent e) {
            int index = monthList.getLeadSelectionIndex();
            if (index > 0) {
              monthList.setSelectedIndex(index - 1);
            } else {
              LocalDate d = monthList.getModel().getElementAt(0).minusDays(1);
              updateMonthView(getCurrentLocalDate().minusMonths(1));
              monthList.setSelectedValue(d, false);
            }
          }
        });
        am.put("selectNextIndex", new AbstractAction() {
          @Override public void actionPerformed(ActionEvent e) {
            int index = monthList.getLeadSelectionIndex();
            if (index < monthList.getModel().getSize() - 1) {
              monthList.setSelectedIndex(index + 1);
            } else {
              LocalDate d = monthList.getModel().getElementAt(monthList.getModel().getSize() - 1).plusDays(1);
              updateMonthView(getCurrentLocalDate().plusMonths(1));
              monthList.setSelectedValue(d, false);
            }
          }
        });
        Action selectPreviousRow = am.get("selectPreviousRow");
        am.put("selectPreviousRow", new AbstractAction() {
          @Override public void actionPerformed(ActionEvent e) {
            int index = monthList.getLeadSelectionIndex();
            int dowvl = DayOfWeek.values().length; // 7
            if (index < dowvl) {
              LocalDate d = monthList.getModel().getElementAt(index).minusDays(dowvl);
              updateMonthView(getCurrentLocalDate().minusMonths(1));
              monthList.setSelectedValue(d, false);
            } else {
              selectPreviousRow.actionPerformed(e);
            }
          }
        });
        Action selectNextRow = am.get("selectNextRow");
        am.put("selectNextRow", new AbstractAction() {
          @Override public void actionPerformed(ActionEvent e) {
            int index = monthList.getLeadSelectionIndex();
            int dowvl = DayOfWeek.values().length; // 7
            if (index > monthList.getModel().getSize() - dowvl) {
              LocalDate d = monthList.getModel().getElementAt(index).plusDays(dowvl);
              updateMonthView(getCurrentLocalDate().plusMonths(1));
              monthList.setSelectedValue(d, false);
            } else {
              selectNextRow.actionPerformed(e);
            }
          }
        });
      }
      public void updateMonthView(LocalDate localDate) {
          currentLocalDate = localDate;
          yearMonthLabel.setText(localDate.format(DateTimeFormatter.ofPattern("YYYY / MM").withLocale(Locale.getDefault())));
          monthList.setModel(new CalendarViewListModel(localDate));
      }

    private class CalendarListRenderer implements ListCellRenderer<LocalDate> {
      private final ListCellRenderer<? super LocalDate> renderer = new DefaultListCellRenderer();

      @Override public Component getListCellRendererComponent(JList<? extends LocalDate> list, LocalDate value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        l.setOpaque(true);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setText(Objects.toString(value.getDayOfMonth()));
        Color fgc = l.getForeground();
        Color bgc = l.getBackground();
        if (YearMonth.from(value).equals(YearMonth.from(getCurrentLocalDate()))) {
          DayOfWeek dow = value.getDayOfWeek();
          if (value.isEqual(realLocalDate)) {
            l.setBackground(new Color(100,255,100,180)); 
          } else if (dow == DayOfWeek.SUNDAY) {
            fgc = new Color(255, 100, 100);
          } else if (dow == DayOfWeek.SATURDAY) {
            fgc = new Color(100, 100, 255);
          }
        } else {
          fgc = Color.GRAY;
        }
        if (isSelected) {
          bgc = new Color(229, 204, 255);
        }

        if (value.isBefore(realLocalDate) || (minDate != null && value.compareTo(minDate)<0)) {
          bgc = Color.LIGHT_GRAY;
          l.setBorder(null);
        }

        l.setBackground(bgc);
        l.setForeground(fgc);
        return l;
      }
    }
  }

  class CalendarViewListModel extends AbstractListModel<LocalDate> {
    public static final int ROW_COUNT = 6;
    private final LocalDate startDate;
    private final WeekFields weekFields = WeekFields.of(Locale.getDefault());

    protected CalendarViewListModel(LocalDate date) {
      super();
      LocalDate firstDayOfMonth = YearMonth.from(date).atDay(1);
      int dowv = firstDayOfMonth.get(weekFields.dayOfWeek()) - 1;
      startDate = firstDayOfMonth.minusDays(dowv);
    }

    @Override public int getSize() {
      return DayOfWeek.values().length * ROW_COUNT;
    }

    @Override public LocalDate getElementAt(int index) {
      return startDate.plusDays(index);
    }
}
