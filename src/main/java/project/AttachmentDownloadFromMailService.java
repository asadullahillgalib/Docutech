package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import com.sun.mail.util.MailSSLSocketFactory;



public class AttachmentDownloadFromMailService extends DownloadFilesFromGoogleDriveUrl{

	private DownloadFilesFromOneDriveUrl serveOneDrive;

	private String saveDirectory;

	public void setSaveDirectory(String dir) {
		this.saveDirectory = dir;

	}

	public void downloadEmailAttachments(String host, String port, String userName, String password) throws Exception {
		Properties properties = new Properties();

		/* for dac-mail */
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);

		properties.put("mail.imap.ssl.trust", "*");// change pop3 with imap for imap
		properties.put("mail.imap.ssl.socketFactory", sf);// change pop3 with imap for imap
		/* for dac-mail */

		properties.put("mail.imap.fetchsize", "819200");
		// server setting
		properties.put("mail.imap.host", host);// change pop3 with imap for imap
		properties.put("mail.imap.port", port);// change pop3 with imap for imap

		// SSL setting
		properties.setProperty("mail.imap.socketFactory.class", // change pop3 with imap for imap
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");// change pop3 with imap for imap
		properties.setProperty("mail.imap.ssl.enable", "true");
		properties.setProperty("mail.imap.socketFactory.port", String.valueOf(port));// change pop3 with imap for imap

		Session session = Session.getDefaultInstance(properties);

		try {
// connects to the message store
			Store store = session.getStore("imap");
			store.connect(userName, password);

// opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_WRITE);

// fetches new messages from server
			// Message[] arrayMessages = folderInbox.getMessages();
			Message[] arrayMessages = folderInbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

			if (arrayMessages.length == 0) {
				System.out.println("No new message");
			} else {
				for (int i = 0; i < arrayMessages.length; i++) {

					Message message = arrayMessages[i];
					message.setFlag(Flags.Flag.SEEN, true);
					saveAttachment(message);

				}
			}

// disconnect
			folderInbox.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for imap."+ ex);

		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store"+ ex);

		} catch (IOException ex) {
			System.out.println("Input output Exception"+ ex);
		}

	}

	public void saveAttachment(Part p) throws Exception {

		if (p.isMimeType("text/html")) {
			getLinkFromHtml(p);
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				saveAttachment(mp.getBodyPart(i));
		}
		// check if the content is a nested message
		else if (p.isMimeType("message/rfc822")) {
			saveAttachment((Part) p.getContent());
		}
		// check if content has pdf
		if (p.isMimeType("application/pdf")) {

			String fileName = MimeUtility.decodeText(p.getFileName());
			System.out.println("File name :" + fileName);

			((MimeBodyPart) p).saveFile(saveDirectory + File.separator + fileName);
		}

	}

	private void getLinkFromHtml(Part p) throws IOException, MessagingException {
		ArrayList<String> googleDriveLinks = new ArrayList<String>();
		ArrayList<String> oneDriveLinks = new ArrayList<String>();
		Pattern linkPattern1 = Pattern.compile("href=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		String desc = p.getContent().toString();

		Matcher pageMatcher1 = linkPattern1.matcher(desc);

		// Search for google drive link
		while (pageMatcher1.find()) {
			if (pageMatcher1.toString().contains("drive.google.com"))
				googleDriveLinks.add(pageMatcher1.group(1));
			// Search for one drive link
			if (pageMatcher1.toString().contains("onedrive.live.com")
					|| pageMatcher1.toString().contains("https://1drv.ms/"))
				oneDriveLinks.add(pageMatcher1.group(1));

		}

		for (String temp : oneDriveLinks) {
			System.out.println(temp);
			// NOTE: This class has Autowired with the class DownloadFilesFromOneDriveUrl
			serveOneDrive.downloadFilesFromOneDriveUrl(temp);

		}
		for (String temp : googleDriveLinks) {
			System.out.println(temp);
			AttachmentDownloadFromMailService servGoogleDrive=new AttachmentDownloadFromMailService();
			// NOTE: This class has Autowired with the class DownloadFileFromGoogleDriveUrl
			servGoogleDrive.downloadFileFromGoogledrive(temp);

		}

	}

	public void downloadAttachments() throws Exception {
		String host = "mail.ntdac.naztech.local";
		String port = "993";
		String userName = "einvoice.test@naztech.us.com";
		String password = "n@ztech.test123";

		String saveDirectory = "C:\\temporary\\";

		setSaveDirectory(saveDirectory);
		downloadEmailAttachments(host, port, userName, password);

	}

}