package project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;



import com.asprise.ocr.Ocr;
import com.asprise.util.pdf.PDFReader;




public class CompanyNameIdentificationService {
	private static String festoolInboxPath = "C:\\\\Users\\\\asadullah.galib\\\\hotfolder\\New folder\\\\Festool\\";
	private static String nmbsInboxPath = "C:\\Users\\asadullah.galib\\hotfolder\\New folder\\NMBS\\";
	private static String mailInbox = "C:\\\\Users\\\\asadullah.galib\\\\hotfolder\\itext\\";

	public void logoDetection() throws Exception {
		List<File> festoolAttachments = new ArrayList<File>();
		List<File> nmbsAttachments = new ArrayList<File>();
		File[] fileList = new File(mailInbox).listFiles();
		System.out.println("Scanning for files in Itext Inbox folder");
		if (fileList == null || fileList.length < 1) {
			System.out.println("No New File in Itext inbox folder");
			return;
		}
		for (File pdf : fileList) {

			PDFReader reader = new PDFReader((new File(mailInbox + pdf.getName())));
			reader.open();
			int pages = reader.getNumberOfPages();
			for (int i = 0; i < pages; i++) {
				Ocr.setUp();
				Ocr ocr = new Ocr();
				ocr.startEngine("eng", Ocr.SPEED_FASTEST);
				String text = ocr.recognize(new File[] { new File(mailInbox + pdf.getName()) }, Ocr.RECOGNIZE_TYPE_ALL,
						Ocr.OUTPUT_FORMAT_PLAINTEXT, 0, null);
				if (text.contains("FEST-J]")) {
					festoolAttachments.add(pdf);
				}
				if (text.contains("INDEPENDENTS")) {
					nmbsAttachments.add(pdf);
				}

				ocr.stopEngine();
			}

			reader.close();

		}
		moveToFestoolFolderPath(festoolAttachments);
		moveToNMBSFolderPath(nmbsAttachments);

	}

	private void moveToFestoolFolderPath(List<File> attachments) {
		String festoolInbox = festoolInboxPath + File.separator;
		File abbDir = new File(festoolInbox);
		if (!abbDir.exists())
			abbDir.mkdir();

		for (File doc : attachments) {
			if (!new File(festoolInbox + File.separator + doc.getName()).exists())
				try {
					java.nio.file.Files.move(Paths.get(doc.getAbsolutePath()),
							Paths.get(festoolInbox + File.separator + doc.getName()),
							StandardCopyOption.REPLACE_EXISTING);
					System.out.println(doc.getName() + " File moved to festool Inbox");
				} catch (IOException e) {
					System.out.println(doc.getName() + " File couldn't move to festool Inbox" + e);
				}
		}
	}

	private void moveToNMBSFolderPath(List<File> attachments) {
		String nmbsInbox = nmbsInboxPath + File.separator;
		File itextDir = new File(nmbsInbox);
		if (!itextDir.exists())
			itextDir.mkdir();

		for (File doc : attachments) {
			if (!new File(nmbsInbox + File.separator + doc.getName()).exists())
				try {
					java.nio.file.Files.move(Paths.get(doc.getAbsolutePath()),
							Paths.get(nmbsInbox + File.separator + doc.getName()), StandardCopyOption.REPLACE_EXISTING);
					System.out.println(doc.getName() + " File moved to nmbs Inbox");

				} catch (IOException e) {
					System.out.println(doc.getName() + " File couldn't move to nmbs Inbox" + e);

				}
		}
	}

}
