package saturday.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifInteropDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.png.PngDirectory;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class FileUtils {


    /**
     * Given a resource (i.e. myFile.xml in /src/main/resource/), read the file into a string builder
     * @param resource The resource to be read
     * @return stringBuilder of the file
     * @throws IOException If there is a failure to get the resource URI
     */
    public static StringBuilder toStringBuilder(Resource resource) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader r = Files.newBufferedReader(Paths.get(resource.getURI()), StandardCharsets.UTF_8)) {
            r.lines().forEach(stringBuilder::append);
        }

        return stringBuilder;
    }

    public static Date getDate(BufferedInputStream ioStream) throws ImageProcessingException, IOException {
        Metadata metadata;
        metadata = ImageMetadataReader.readMetadata(ioStream);

        // First attempt to get the datetime from exif ifd0 directory
        Date datetimeOriginal = null;
        ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if(ifd0Directory != null) {
            datetimeOriginal = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL);

            if(datetimeOriginal == null) {
                datetimeOriginal = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME_DIGITIZED);
            }

            if(datetimeOriginal == null) {
                datetimeOriginal = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME);
            }
        }

        // if that fails, fallback by prioritized list of possible directories
        if(datetimeOriginal == null) {
            ExifSubIFDDirectory subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if(subIFDDirectory != null) {
                datetimeOriginal = subIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }
        }

        if(datetimeOriginal == null) {
            IptcDirectory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);

            if(iptcDirectory != null) {
                datetimeOriginal = iptcDirectory.getDate(IptcDirectory.TAG_DATE_CREATED);
            }
        }

        if(datetimeOriginal == null) {
            ExifInteropDirectory interopDirectory = metadata.getFirstDirectoryOfType(ExifInteropDirectory.class);

            if(interopDirectory != null) {
                datetimeOriginal = interopDirectory.getDate(ExifInteropDirectory.TAG_DATETIME_ORIGINAL);
            }
        }
        if(datetimeOriginal == null) {
            ExifInteropDirectory interopDirectory = metadata.getFirstDirectoryOfType(ExifInteropDirectory.class);

            if(interopDirectory != null) {
                datetimeOriginal = interopDirectory.getDate(ExifInteropDirectory.TAG_DATETIME_DIGITIZED);
            }
        }

        if(datetimeOriginal == null) {
            ExifInteropDirectory interopDirectory = metadata.getFirstDirectoryOfType(ExifInteropDirectory.class);

            if(interopDirectory != null) {
                datetimeOriginal = interopDirectory.getDate(ExifInteropDirectory.TAG_DATETIME);
            }
        }

        if(datetimeOriginal == null) {
            PngDirectory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);

            if(pngDirectory != null) {
                datetimeOriginal = pngDirectory.getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME);
            }
        }

        return datetimeOriginal;
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
