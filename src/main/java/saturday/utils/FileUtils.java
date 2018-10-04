package saturday.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifInteropDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.icc.IccDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.png.PngDirectory;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static saturday.utils.CommonUtils.coalesce;

@SuppressWarnings("WeakerAccess")
public class FileUtils {


    /**
     * Given a resource (i.e. myFile.xml in /src/main/resource/), read the file into a string builder
     * @param resource The resource to be read
     * @return stringBuilder of the file
     * @throws IOException If there is a failure to get the resource URI
     */
    public static StringBuilder toStringBuilder(Resource resource) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Path path = Paths.get(resource.getURI());

        try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            r.lines().forEach(stringBuilder::append);
        }

        return stringBuilder;
    }

    public static Optional<Date> getDate(BufferedInputStream ioStream) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(ioStream);
        ArrayList<Date> dates = new ArrayList<>();

        metadata.getDirectories()
                .forEach(directory ->
                        dates.add(coalesce(
                                directory.getDate(ExifInteropDirectory.TAG_DATETIME_ORIGINAL),
                                directory.getDate(ExifInteropDirectory.TAG_DATETIME_DIGITIZED),
                                directory.getDate(ExifInteropDirectory.TAG_DATETIME),
                                directory.getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME),
                                directory.getDate(IptcDirectory.TAG_DATE_CREATED),
                                directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL),
                                directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL),
                                directory.getDate(ExifIFD0Directory.TAG_DATETIME_DIGITIZED),
                                directory.getDate(ExifIFD0Directory.TAG_DATETIME),
                                directory.getDate(IccDirectory.TAG_PROFILE_DATETIME),
                                directory.getDate(GpsDirectory.TAG_DATE_STAMP)
                        ))
                );

        return dates.stream().filter(Objects::nonNull).findAny();
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

    public static String classpathResourceToString(ClassPathResource cpr) throws IOException {
        String resourceAsString;
        InputStream inputStream = cpr.getInputStream();
        resourceAsString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        inputStream.close();

        return resourceAsString;
    }
}
