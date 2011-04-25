package info.jo32.SGKClient.Window;

import info.jo32.SGKClient.Config.Config;
import info.jo32.SGKClient.Util.FileWordsConverter;
import info.jo32.SGKClient.Util.ObjectURLLinker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.graphics.Cursor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.custom.StyledText;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;

import us.bandj.SimpleGlossaryKiller.Controller.Signal;
import us.bandj.SimpleGlossaryKiller.Controller.WordView;
import org.eclipse.swt.events.MouseAdapter;

public class MainWindows {

	protected Shell shlMainWindow;

	Display disp;

	Cursor cursor = new Cursor(disp, SWT.CURSOR_HAND);

	private List<WordView> words = null;

	private String[] usrAndPwd = null;

	private Label statusLabel = null;

	private Label wordName = null;

	private Label lang = null;

	private StyledText wordDef = null;

	private String[] defs = null;

	private String[] phoneticSymbols = null;

	private int index = 0;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindows window = new MainWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		this.disp = display;
		createContents();
		shlMainWindow.open();
		shlMainWindow.layout();
		shlMainWindow.setEnabled(false);

		Login l = new Login(shlMainWindow, SWT.CLOSE);
		String[] usrAndPwd = (String[]) l.open();

		this.setUsrAndPwd(usrAndPwd);

		class GetWordsTask extends Thread {

			private String[] usrAndPwd = null;

			public GetWordsTask(String[] usrAndPwd) {
				this.usrAndPwd = usrAndPwd;
			}

			public void run() {

				// GetWords Task contents.
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						// task1
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								statusLabel.setText("getting words...");
							}

						});

						// task2
						ObjectURLLinker<List<WordView>> oul = null;
						try {
							String[] paramNames = { "username", "password" };
							oul = new ObjectURLLinker<List<WordView>>(Config.GET_WORDS_URL, paramNames, usrAndPwd);
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						try {
							Type t = new TypeToken<List<WordView>>() {
							}.getType();
							words = oul.getObjectByURL(t);
							defs = new String[words.size()];
							phoneticSymbols = new String[words.size()];
						} catch (java.net.ConnectException e1) {
						} catch (IOException e) {
						}

						// task 3
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								statusLabel.setText("done getting words.");
								if (words.size() > 0)
									setWord(0);
								else
									statusLabel.setText("done leaning these words.");
							}
						});

					}
				});

			}
		}

		Display.getDefault().asyncExec(new GetWordsTask(this.getUsrAndPwd()));

		shlMainWindow.setEnabled(true);

		while (!shlMainWindow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMainWindow = new Shell(SWT.CLOSE | SWT.MIN);
		shlMainWindow.setSize(500, 500);
		shlMainWindow.setMinimumSize(new Point(500, 300));
		shlMainWindow.setText("Main Window");

		Label label = new Label(shlMainWindow, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 435, 474, 4);

		Label lblStatus = new Label(shlMainWindow, SWT.NONE);
		lblStatus.setBounds(10, 447, 44, 15);
		lblStatus.setText("status :");

		ToolBar toolBar = new ToolBar(shlMainWindow, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(10, 10, 474, 40);

		ToolItem tltmUpload = new ToolItem(toolBar, SWT.NONE);
		tltmUpload.setImage(SWTResourceManager.getImage(MainWindows.class, "/sgk_icons/Upload-icon.png"));
		tltmUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shlMainWindow, SWT.NULL);
				String path = dialog.open();
				new Uploading(statusLabel, usrAndPwd, path).start();
			}
		});
		tltmUpload.setText("Upload Words");

		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.setImage(SWTResourceManager.getImage(MainWindows.class, "/sgk_icons/Sync-icon.png"));
		tltmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Saving(statusLabel, words, usrAndPwd).start();
			}
		});
		tltmSave.setText("Save");

		ToolItem tltmPrevious = new ToolItem(toolBar, SWT.NONE);
		tltmPrevious.setImage(SWTResourceManager.getImage(MainWindows.class, "/sgk_icons/Signs-Arrow-Left-icon.png"));
		tltmPrevious.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				goBack();
			}
		});
		tltmPrevious.setText("Previous");

		ToolItem tltmNext = new ToolItem(toolBar, SWT.NONE);
		tltmNext.setImage(SWTResourceManager.getImage(MainWindows.class, "/sgk_icons/Signs-Arrow-Right-icon.png"));
		tltmNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				goNext();
			}
		});
		tltmNext.setText("Next");

		ToolItem tltmHelp = new ToolItem(toolBar, SWT.NONE);
		tltmHelp.setImage(SWTResourceManager.getImage(MainWindows.class, "/sgk_icons/Alarm-Help-and-Support-icon.png"));
		tltmHelp.setText("Help");

		Label lblStatus1 = new Label(shlMainWindow, SWT.NONE);
		this.statusLabel = lblStatus1;
		lblStatus1.setText("normal");
		lblStatus1.setBounds(55, 447, 429, 15);

		Label label_5 = new Label(shlMainWindow, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_5.setBounds(10, 56, 474, 2);

		Composite composite = new Composite(shlMainWindow, SWT.NONE);
		composite.setBounds(10, 64, 474, 340);

		Label lblTtt = new Label(composite, SWT.NONE);
		this.wordName = lblTtt;
		lblTtt.setForeground(SWTResourceManager.getColor(0, 0, 128));
		lblTtt.setText("");
		lblTtt.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblTtt.setBounds(73, 0, 231, 30);

		Label label_2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setBounds(0, 30, 474, 2);

		Label lblWord = new Label(composite, SWT.NONE);
		lblWord.setFont(SWTResourceManager.getFont("Segoe UI", 15, SWT.NORMAL));
		lblWord.setBounds(0, 0, 67, 30);
		lblWord.setText("Word :");

		StyledText styledText = new StyledText(composite, SWT.BORDER);
		this.wordDef = styledText;
		styledText.setBounds(0, 44, 474, 69);

		final Button btnShowAnswer = new Button(composite, SWT.NONE);
		btnShowAnswer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (words.size() > 0) {
					// GettingDefiniationOfWord(Label wordName, Label lang,
					// Label statusLabel, StyledText wordDef, String[] defs,
					// String[] phoneticSymbols, List<WordView> words, int
					// index)
					GettingDefiniationOfWord gd = new GettingDefiniationOfWord(wordName, lang, statusLabel, wordDef, defs, phoneticSymbols, words, index);
					gd.start();
				}
			}
		});
		btnShowAnswer.setBounds(0, 119, 474, 25);
		btnShowAnswer.setText("show answer");

		Label lblRate = new Label(composite, SWT.CENTER);
		lblRate.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblRate.setBounds(0, 150, 54, 33);
		lblRate.setText("Rate :");

		final Label label_3 = new Label(composite, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String score = ((Label) e.getSource()).getText();
				rateAndGoNext(score);
			}
		});
		label_3.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelOnFocus(l);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelLoseFocus(l);
			}
		});
		label_3.setCursor(cursor);
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_3.setFont(SWTResourceManager.getFont("Segoe UI", 33, SWT.NORMAL));
		label_3.setBounds(10, 189, 86, 70);
		label_3.setText("1");

		final Label label_4 = new Label(composite, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String score = ((Label) e.getSource()).getText();
				rateAndGoNext(score);
			}
		});
		label_4.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelOnFocus(l);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelLoseFocus(l);
			}
		});
		label_4.setCursor(cursor);
		label_4.setText("2");
		label_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_4.setFont(SWTResourceManager.getFont("Segoe UI", 33, SWT.NORMAL));
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_4.setBounds(102, 189, 86, 70);

		final Label label_6 = new Label(composite, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String score = ((Label) e.getSource()).getText();
				rateAndGoNext(score);
			}
		});
		label_6.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelOnFocus(l);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelLoseFocus(l);
			}
		});
		label_6.setCursor(cursor);
		label_6.setText("3");
		label_6.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_6.setFont(SWTResourceManager.getFont("Segoe UI", 33, SWT.NORMAL));
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_6.setBounds(194, 189, 86, 70);

		final Label label_7 = new Label(composite, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String score = ((Label) e.getSource()).getText();
				rateAndGoNext(score);
			}
		});
		label_7.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelOnFocus(l);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelLoseFocus(l);
			}
		});
		label_7.setCursor(cursor);
		label_7.setText("4");
		label_7.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_7.setFont(SWTResourceManager.getFont("Segoe UI", 33, SWT.NORMAL));
		label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_7.setBounds(286, 189, 86, 70);

		final Label label_8 = new Label(composite, SWT.BORDER | SWT.SHADOW_NONE | SWT.CENTER);
		label_8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String score = ((Label) e.getSource()).getText();
				rateAndGoNext(score);
			}
		});
		label_8.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelOnFocus(l);
			}

			@Override
			public void mouseExit(MouseEvent e) {
				Label l = (Label) e.getSource();
				labelLoseFocus(l);
			}
		});
		label_8.setCursor(cursor);
		label_8.setText("5");
		label_8.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_8.setFont(SWTResourceManager.getFont("Segoe UI", 33, SWT.NORMAL));
		label_8.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_8.setBounds(378, 189, 86, 70);

		Label lblDidntKnow = new Label(composite, SWT.NONE);
		lblDidntKnow.setBounds(12, 267, 84, 63);
		lblDidntKnow.setText("didn't know");

		Label lblGuessedWithNo = new Label(composite, SWT.WRAP);
		lblGuessedWithNo.setText("guessed right with no thought");
		lblGuessedWithNo.setBounds(102, 267, 86, 63);

		Label lblGuessedWithThought = new Label(composite, SWT.WRAP);
		lblGuessedWithThought.setText("guessed right with thought");
		lblGuessedWithThought.setBounds(194, 267, 86, 63);

		Label lblRememberIt = new Label(composite, SWT.WRAP);
		lblRememberIt.setText("remember it");
		lblRememberIt.setBounds(286, 267, 86, 63);

		Label lblFeelBored = new Label(composite, SWT.WRAP);
		lblFeelBored.setText("feel bored about it");
		lblFeelBored.setBounds(378, 267, 86, 63);

		Label label_1 = new Label(composite, SWT.NONE);
		this.lang = label_1;
		label_1.setText("");
		label_1.setForeground(SWTResourceManager.getColor(0, 0, 128));
		label_1.setFont(SWTResourceManager.getFont("Kingsoft Phonetic Plain", 15, SWT.NORMAL));
		label_1.setBounds(310, 0, 164, 30);

		btnShowAnswer.setFocus();

		disp.addFilter(SWT.KeyDown, new Listener() {

			@Override
			public void handleEvent(Event e) {

				if (shlMainWindow.isEnabled()) {

					switch (e.keyCode) {
					case SWT.KEYPAD_1:
						labelOnFocus(label_3);
						rateAndGoNext("1");
						break;
					case SWT.KEYPAD_2:
						labelOnFocus(label_4);
						rateAndGoNext("2");
						break;
					case SWT.KEYPAD_3:
						labelOnFocus(label_6);
						rateAndGoNext("3");
						break;
					case SWT.KEYPAD_4:
						labelOnFocus(label_7);
						rateAndGoNext("4");
						break;
					case SWT.KEYPAD_5:
						labelOnFocus(label_8);
						rateAndGoNext("5");
						break;
					case SWT.ARROW_RIGHT:
						goNext();
						break;
					case SWT.ARROW_LEFT:
						goBack();
						break;
					// the keypad enter
					case 16777296:
						btnShowAnswer.setFocus();
						GettingDefiniationOfWord gd = new GettingDefiniationOfWord(wordName, lang, statusLabel, wordDef, defs, phoneticSymbols, words, index);
						gd.start();
						break;
					}
				}
			}
		});

		disp.addFilter(SWT.KeyUp, new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (shlMainWindow.isEnabled()) {

					switch (e.keyCode) {
					case SWT.KEYPAD_1:
						labelLoseFocus(label_3);
						break;
					case SWT.KEYPAD_2:
						labelLoseFocus(label_4);
						break;
					case SWT.KEYPAD_3:
						labelLoseFocus(label_6);
						break;
					case SWT.KEYPAD_4:
						labelLoseFocus(label_7);
						break;
					case SWT.KEYPAD_5:
						labelLoseFocus(label_8);
						break;
					}
				}
			}
		});
	}

	public void goNext() {
		if (index < this.words.size() - 1) {
			this.index += 1;
			this.setWord(index);
			this.wordDef.setText("");
			this.lang.setText("");
		}
	}

	public void goBack() {
		if (index > 0) {
			this.index -= 1;
			this.setWord(index);
			this.wordDef.setText("");
			this.lang.setText("");
		}
	}

	public void rateAndGoNext(String score) {
		if (words.size() > 0) {
			int _score = Integer.parseInt(score);
			this.words.get(index).setFamiliarity(_score);
			if (index < this.words.size() - 1) {
				this.index += 1;
				this.setWord(index);
				this.wordDef.setText("");
			}
		}
	}

	public void setWord(int i) {
		this.wordName.setText(this.words.get(i).getWord() + " (rate: " + this.words.get(i).getFamiliarity() + ")");
	}

	public void labelOnFocus(Label l) {
		l.setBackground(SWTResourceManager.getColor(50, 205, 50));
	}

	public void labelLoseFocus(Label l) {
		l.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
	}

	public List<WordView> getWords() {
		return words;
	}

	public void setWords(List<WordView> words) {
		this.words = words;
	}

	public String[] getUsrAndPwd() {
		return usrAndPwd;
	}

	public void setUsrAndPwd(String[] usrAndPwd) {
		this.usrAndPwd = usrAndPwd;
	}
}

class GettingDefiniationOfWord extends Thread {

	Label wordName = null;
	Label lang = null;
	Label statusLabel = null;
	List<WordView> words = null;
	StyledText wordDef = null;
	String[] defs = null;
	String word = null;
	String[] phoneticSymbols = null;
	int index = 0;

	public GettingDefiniationOfWord(Label wordName, Label lang, Label statusLabel, StyledText wordDef, String[] defs, String[] phoneticSymbols, List<WordView> words, int index) {
		this.phoneticSymbols = phoneticSymbols;
		this.wordName = wordName;
		this.lang = lang;
		this.statusLabel = statusLabel;
		this.words = words;
		this.wordDef = wordDef;
		this.defs = defs;
		this.index = index;
		this.word = words.get(index).getWord();
	}

	public void run() {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				statusLabel.setText("Getting Definiation of " + word + "...");
			}
		});
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(Config.DICTDOTCN_XML_URL + word);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element e = document.getRootElement();
		final Element _this = e.element("def");
		final Element pSymbol = e.element("pron");
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (_this != null) {
					defs[index] = _this.getText();
					phoneticSymbols[index] = pSymbol.getText();
					wordDef.setText(defs[index]);
					lang.setText("/ " + phoneticSymbols[index] + " /");
					statusLabel.setText("Have Got Definiation of \"" + word + "\"...");
				} else {
					String hint = "This word doesn't exist in dict.cn.";
					defs[index] = hint;
					wordDef.setText(hint);
					statusLabel.setText(hint);
				}
			}
		});

	}

}

class Uploading extends Thread {

	private Label statusLabel = null;
	private String path = null;
	private List<String> words = null;
	private Signal s = null;
	private String[] paramNames = { "username", "password", "words" };
	private String[] usrAndPwd = null;

	public Uploading(Label statusLabel, String[] usrAndPwd, String path) {
		this.statusLabel = statusLabel;
		this.usrAndPwd = usrAndPwd;
		this.path = path;
	}

	public void run() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				statusLabel.setText("uploading...");
			}
		});
		FileWordsConverter fwc = new FileWordsConverter(path);
		try {
			List<String> _words = fwc.getWordsString();
			this.words = _words;
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < this.words.size(); i++) {

			String[] paramValues = { usrAndPwd[0], usrAndPwd[1], this.words.get(i) };
			ObjectURLLinker<Signal> oul = null;
			try {
				oul = new ObjectURLLinker<Signal>(Config.ADD_WORDS_URL, this.paramNames, paramValues);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				s = oul.getObjectByURL(Signal.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final int k = i;
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (s.getCode() == 1) {
						statusLabel.setText("uploaded about " + (k + 1) * 500 + " words.");
					} else {
						statusLabel.setText((String) s.getMessage());
					}
				}
			});
		}

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (s.getCode() == 1) {
					statusLabel.setText("uploaded, please restart your app.");
				} else {
					statusLabel.setText((String) s.getMessage());
				}
			}
		});

	}
}

class Saving extends Thread {

	private Label statusLabel = null;
	private List<WordView> words = null;
	private Signal s = null;
	private String[] paramNames = { "username", "password", "words" };
	private String[] usrAndPwd = null;

	public Saving(Label statusLabel, List<WordView> words, String[] usrAndPwd) {
		this.statusLabel = statusLabel;
		this.words = words;
		this.usrAndPwd = usrAndPwd;
	}

	public void run() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				statusLabel.setText("saving...");
			}
		});
		Gson g = new Gson();
		String _words = g.toJson(this.words);
		String[] paramValues = { usrAndPwd[0], usrAndPwd[1], _words };
		ObjectURLLinker<Signal> oul = null;
		try {
			oul = new ObjectURLLinker<Signal>(Config.CHANGE_WORDS_URL, this.paramNames, paramValues);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			s = oul.getObjectByURL(Signal.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (s.getCode() == 1) {
					statusLabel.setText("saved");
				} else {
					statusLabel.setText((String) s.getMessage());
				}
			}
		});

	}
}
