package saturday.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class FileUtils {

    public static Date getDate(InputStream ioStream) throws ImageProcessingException, IOException {
        Metadata metadata;
        metadata = ImageMetadataReader.readMetadata(ioStream);
        // obtain the Exif directory
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        // query the tag's value
        return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }


    public static byte[] decodeBase64(String data) {
        // strip base64 data prefix
        int i = data.indexOf(",");

        if(i > -1) {
            data = data.substring(i + 1);
        }

        // get input stream for upload
        return java.util.Base64.getDecoder().decode(data);
    }
}
