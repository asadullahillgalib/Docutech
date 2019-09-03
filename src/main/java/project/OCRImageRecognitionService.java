package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdfText.PDFText;


public class OCRImageRecognitionService {

	private String abbyInboxPath="C:\\Users\\asadullah.galib\\hotfolder\\abby\\";
	private String iTextInboxPath="C:\\Users\\asadullah.galib\\hotfolder\\itext\\";
	private String mailInboxPath="C:\\Users\\asadullah.galib\\hotfolder\\inbox\\";

	public void imageRecognition() throws PDFException, IOException {
		List<File> itextattachments = new ArrayList<File>();
		List<File> abbytattachments = new ArrayList<File>();
		File[] fileList = new File(mailInboxPath).listFiles();
		if (fileList == null || fileList.length < 1)
			return;
		for (File pdf : fileList) {
			InputStream inputStream = new FileInputStream(pdf.getAbsolutePath());
			PDFText pdfText = new PDFText(inputStream, null);
			int pageCount = pdfText.getPageCount();
			for (int i = 0; i < pageCount; i++) {

				String pageText = pdfText.getText(i);

				if (pageText != null && pageText.trim().length() > 0) {
					itextattachments.add(pdf);
					break;
				} else {
					abbytattachments.add(pdf);
				}
			}
			inputStream.close();
		}
		moveToItextInbox(itextattachments);
		moveToABBYInbox(abbytattachments);
	}

	private void moveToABBYInbox(List<File> attachments) {
		String abbyInbox = abbyInboxPath + File.separator;
		File abbDir = new File(abbyInbox);
		if (!abbDir.exists())
			abbDir.mkdir();

		for (File doc : attachments) {
			if (!new File(abbyInbox + File.separator + doc.getName()).exists())
				try {
					Files.move(Paths.get(doc.getAbsolutePath()), Paths.get(abbyInbox + File.separator + doc.getName()),
							StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Files moved to Abbyy Inbox");
				} catch (IOException e) {
					System.out.println("Files couldn't move to Abbyy Inbox"+ e);
				}
		}
	}

	private void moveToItextInbox(List<File> attachments) {
		String itextInbox = iTextInboxPath + File.separator;
		File itextDir = new File(itextInbox);
		if (!itextDir.exists())
			itextDir.mkdir();

		for (File doc : attachments) {
			if (!new File(itextInbox + File.separator + doc.getName()).exists())
				try {
					Files.move(Paths.get(doc.getAbsolutePath()), Paths.get(itextInbox + File.separator + doc.getName()),
							StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Files moved to Itext Inbox");
				} catch (IOException e) {
					System.out.println("Files couldn't move to Itext Inbox"+ e);
				}
		}
	}
}
