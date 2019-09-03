package project;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;



public class DownloadFilesFromFtpServerService {

	private int portNo=21;
	private String ftpUrl="10.33.44.31";
	private String userName="test-01";
	private String pass="test@123";
	private String downloadPath="C:\\Users\\asadullah.galib\\Desktop\\ga\\";


	public void filesDownloadFromFtp() throws IOException {
		FTPClient ftpClient = new FTPClient();
		try {

			ftpClient.connect(ftpUrl, portNo);
			ftpClient.login(userName, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			FTPFile[] files = ftpClient.listFiles("/upload", new FTPFileFilter() {
				@Override
				public boolean accept(FTPFile file) {

					return (file.isFile() && file.getName().endsWith(".pdf"));
				}
			});
			



			if (files.length == 0) {
				System.out.println("No new files");
			} else {
				for (FTPFile file : files) {
					String remoteFile1 = "/upload/";
					String remoteFile2 = "/download/";
					OutputStream outputStream1 = new BufferedOutputStream(
							new FileOutputStream(downloadPath + file.getName()));
					boolean success = ftpClient.retrieveFile(remoteFile1 + file.getName(), outputStream1);
					outputStream1.close();
					if (success) {
						System.out.println("File " + file.getName() +  " has been downloaded successfully.");
						boolean removeSuccess = ftpClient.rename(remoteFile1 + file.getName(),
								remoteFile2 + file.getName());
						if (removeSuccess) {
							System.out.println("File has been removed to download " );
						} else {
							System.out.println("File is not removed");
						}

					}

				}
			}

		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
