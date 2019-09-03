package project;

public class MainClass {

	public static void main(String[] args) throws Exception {

		OCRImageRecognitionService ocrserv = new OCRImageRecognitionService();
		DownloadFilesFromFtpServerService ftpserv = new DownloadFilesFromFtpServerService();
		CompanyNameIdentificationService cnserv = new CompanyNameIdentificationService();
		AttachmentDownloadFromMailService attdserv = new AttachmentDownloadFromMailService();

		ocrserv.imageRecognition();
		ftpserv.filesDownloadFromFtp();
		cnserv.logoDetection();
		attdserv.downloadAttachments();

	}

}
