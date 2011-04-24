package info.jo32.SGKClient.Window;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import info.jo32.SGKClient.Config.Config;
import info.jo32.SGKClient.Util.ObjectURLLinker;
import info.jo32.SGKClient.Util.PropertyLoader;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import us.bandj.SimpleGlossaryKiller.Controller.Signal;
import org.eclipse.swt.custom.StyledText;
import com.swtdesigner.SWTResourceManager;

public class Login extends Dialog {

	protected Object result;
	protected Shell shlLoginDialog;
	protected Shell pShell;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public Login(Shell parent, int style) {

		super(parent, style);
		this.pShell = parent;
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlLoginDialog.open();
		shlLoginDialog.layout();
		Display display = getParent().getDisplay();
		while (!shlLoginDialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlLoginDialog = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shlLoginDialog.setMinimumSize(new Point(200, 100));
		shlLoginDialog.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
			}
		});
		shlLoginDialog.setSize(250, 190);
		shlLoginDialog.setText("Login Dialog");

		Label lblInputTheUsername = new Label(shlLoginDialog, SWT.NONE);
		lblInputTheUsername.setBounds(10, 10, 224, 15);
		lblInputTheUsername.setText("Input the username and password");

		Label lblUsername = new Label(shlLoginDialog, SWT.NONE);
		lblUsername.setBounds(10, 50, 55, 15);
		lblUsername.setText("username:");

		text = new Text(shlLoginDialog, SWT.BORDER);
		text.setBounds(72, 47, 162, 21);

		Label lblPassword = new Label(shlLoginDialog, SWT.NONE);
		lblPassword.setBounds(10, 71, 55, 15);
		lblPassword.setText("password:");

		text_1 = new Text(shlLoginDialog, SWT.BORDER | SWT.PASSWORD);
		text_1.setBounds(72, 71, 162, 21);

		final StyledText styledText = new StyledText(shlLoginDialog, SWT.NONE);
		styledText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		styledText.setBounds(10, 168, 224, 78);

		Button btnOk = new Button(shlLoginDialog, SWT.NONE);
		btnOk.setGrayed(true);

		btnOk.setBounds(72, 98, 75, 25);
		btnOk.setText("OK");

		Button btnCancel = new Button(shlLoginDialog, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pShell.close();
			}
		});
		btnCancel.setBounds(153, 98, 75, 25);
		btnCancel.setText("Cancel");

		Label lblRemember = new Label(shlLoginDialog, SWT.NONE);
		lblRemember.setBounds(72, 135, 65, 15);
		lblRemember.setText("remember?");

		final Button btnYes = new Button(shlLoginDialog, SWT.CHECK);
		btnYes.setBounds(141, 135, 93, 16);
		btnYes.setText("yes");

		new Thread(new Runnable() {

			@Override
			public void run() {

				File file = new File("setting.ini");
				boolean flag = false;

				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					flag = true;
				}

				PropertyLoader pl = null;
				try {
					pl = new PropertyLoader("setting.ini");
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				if (flag == true) {
					String checked = pl.getProperty("checked");
					if (checked != null && checked.equals("yes")) {
						final String username = pl.getProperty("username");
						final String password = pl.getProperty("password");
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								text.setText(username);
								text_1.setText(password);
								btnYes.setSelection(true);
							}
						});
					}
				}

			}
		}).start();

		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				class LoginHandling extends Thread {

					private Signal s = null;
					private String[] _result = { text.getText(), text_1.getText() };
					private String[] paramNames = { "username", "password" };
					private boolean checked = btnYes.getSelection();

					public void run() {

						PropertyLoader pl = null;
						try {
							pl = new PropertyLoader("setting.ini");
						} catch (IOException e2) {
							e2.printStackTrace();
						}

						pl.setProperty("username", _result[0]);
						pl.setProperty("password", _result[1]);

						String _checked;
						if (checked == true) {
							_checked = "yes";
						} else {
							_checked = "no";
						}

						pl.setProperty("checked", _checked);
						
						try {
							pl.makePersistent();
						} catch (IOException e2) {
							e2.printStackTrace();
						}

						ObjectURLLinker<Signal> oul = null;
						try {
							oul = new ObjectURLLinker<Signal>(Config.LOGIN_URL, paramNames, _result);
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						s = null;
						try {
							s = oul.getObjectByURL(Signal.class);
						} catch (java.net.ConnectException e1) {
						} catch (IOException e) {
						}
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								shlLoginDialog.setSize(250, 250);
								if (s == null) {
									styledText.setText("error!");
								} else if (s.getCode() == 0) {
									styledText.setText((String) s.getMessage());
								} else {
									styledText.setText((String) s.getMessage());
									shlLoginDialog.close();
								}
								result = _result;
							}
						});
					}
				}

				new LoginHandling().start();
			}
		});

	}
}