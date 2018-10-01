package saturday.utils;

// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.


import saturday.exceptions.ExtensionNotFound;

import java.util.HashMap;

/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class MimeTypes {

    public static final String MIME_APPLICATION_ANDREW_INSET = "application/andrew-inset";
    public static final String MIME_APPLICATION_JSON = "application/json";
    public static final String MIME_APPLICATION_ZIP = "application/zip";
    public static final String MIME_APPLICATION_X_GZIP = "application/x-gzip";
    public static final String MIME_APPLICATION_TGZ = "application/tgz";
    public static final String MIME_APPLICATION_MSWORD = "application/msword";
    public static final String MIME_APPLICATION_POSTSCRIPT = "application/postscript";
    public static final String MIME_APPLICATION_PDF = "application/pdf";
    public static final String MIME_APPLICATION_JNLP = "application/jnlp";
    public static final String MIME_APPLICATION_MAC_BINHEX40 = "application/mac-binhex40";
    public static final String MIME_APPLICATION_MAC_COMPACTPRO = "application/mac-compactpro";
    public static final String MIME_APPLICATION_MATHML_XML = "application/mathml+xml";
    public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MIME_APPLICATION_ODA = "application/oda";
    public static final String MIME_APPLICATION_RDF_XML = "application/rdf+xml";
    public static final String MIME_APPLICATION_JAVA_ARCHIVE = "application/java-archive";
    public static final String MIME_APPLICATION_RDF_SMIL = "application/smil";
    public static final String MIME_APPLICATION_SRGS = "application/srgs";
    public static final String MIME_APPLICATION_SRGS_XML = "application/srgs+xml";
    public static final String MIME_APPLICATION_VND_MIF = "application/vnd.mif";
    public static final String MIME_APPLICATION_VND_MSEXCEL = "application/vnd.ms-excel";
    public static final String MIME_APPLICATION_VND_MSPOWERPOINT= "application/vnd.ms-powerpoint";
    public static final String MIME_APPLICATION_VND_RNREALMEDIA = "application/vnd.rn-realmedia";
    public static final String MIME_APPLICATION_X_BCPIO = "application/x-bcpio";
    public static final String MIME_APPLICATION_X_CDLINK = "application/x-cdlink";
    public static final String MIME_APPLICATION_X_CHESS_PGN = "application/x-chess-pgn";
    public static final String MIME_APPLICATION_X_CPIO = "application/x-cpio";
    public static final String MIME_APPLICATION_X_CSH = "application/x-csh";
    public static final String MIME_APPLICATION_X_DIRECTOR = "application/x-director";
    public static final String MIME_APPLICATION_X_DVI = "application/x-dvi";
    public static final String MIME_APPLICATION_X_FUTURESPLASH = "application/x-futuresplash";
    public static final String MIME_APPLICATION_X_GTAR = "application/x-gtar";
    public static final String MIME_APPLICATION_X_HDF = "application/x-hdf";
    public static final String MIME_APPLICATION_X_JAVASCRIPT = "application/x-javascript";
    public static final String MIME_APPLICATION_X_KOAN = "application/x-koan";
    public static final String MIME_APPLICATION_X_LATEX = "application/x-latex";
    public static final String MIME_APPLICATION_X_NETCDF = "application/x-netcdf";
    public static final String MIME_APPLICATION_X_OGG = "application/x-ogg";
    public static final String MIME_APPLICATION_X_SH = "application/x-sh";
    public static final String MIME_APPLICATION_X_SHAR = "application/x-shar";
    public static final String MIME_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
    public static final String MIME_APPLICATION_X_STUFFIT = "application/x-stuffit";
    public static final String MIME_APPLICATION_X_SV4CPIO = "application/x-sv4cpio";
    public static final String MIME_APPLICATION_X_SV4CRC = "application/x-sv4crc";
    public static final String MIME_APPLICATION_X_TAR = "application/x-tar";
    public static final String MIME_APPLICATION_X_RAR_COMPRESSED= "application/x-rar-compressed";
    public static final String MIME_APPLICATION_X_TCL = "application/x-tcl";
    public static final String MIME_APPLICATION_X_TEX = "application/x-tex";
    public static final String MIME_APPLICATION_X_TEXINFO = "application/x-texinfo";
    public static final String MIME_APPLICATION_X_TROFF = "application/x-troff";
    public static final String MIME_APPLICATION_X_TROFF_MAN = "application/x-troff-man";
    public static final String MIME_APPLICATION_X_TROFF_ME = "application/x-troff-me";
    public static final String MIME_APPLICATION_X_TROFF_MS = "application/x-troff-ms";
    public static final String MIME_APPLICATION_X_USTAR = "application/x-ustar";
    public static final String MIME_APPLICATION_X_WAIS_SOURCE = "application/x-wais-source";
    public static final String MIME_APPLICATION_VND_MOZZILLA_XUL_XML = "application/vnd.mozilla.xul+xml";
    public static final String MIME_APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final String MIME_APPLICATION_XSLT_XML = "application/xslt+xml";
    public static final String MIME_APPLICATION_XML = "application/xml";
    public static final String MIME_APPLICATION_XML_DTD = "application/xml-dtd";
    public static final String MIME_IMAGE_BMP = "image/bmp";
    public static final String MIME_IMAGE_CGM = "image/cgm";
    public static final String MIME_IMAGE_GIF = "image/gif";
    public static final String MIME_IMAGE_IEF = "image/ief";
    public static final String MIME_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_IMAGE_TIFF = "image/tiff";
    public static final String MIME_IMAGE_PNG = "image/png";
    public static final String MIME_IMAGE_SVG_XML = "image/svg+xml";
    public static final String MIME_IMAGE_VND_DJVU = "image/vnd.djvu";
    public static final String MIME_IMAGE_WAP_WBMP = "image/vnd.wap.wbmp";
    public static final String MIME_IMAGE_X_CMU_RASTER = "image/x-cmu-raster";
    public static final String MIME_IMAGE_X_ICON = "image/x-icon";
    public static final String MIME_IMAGE_X_PORTABLE_ANYMAP = "image/x-portable-anymap";
    public static final String MIME_IMAGE_X_PORTABLE_BITMAP = "image/x-portable-bitmap";
    public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP = "image/x-portable-graymap";
    public static final String MIME_IMAGE_X_PORTABLE_PIXMAP = "image/x-portable-pixmap";
    public static final String MIME_IMAGE_X_RGB = "image/x-rgb";
    public static final String MIME_AUDIO_BASIC = "audio/basic";
    public static final String MIME_AUDIO_MIDI = "audio/midi";
    public static final String MIME_AUDIO_MPEG = "audio/mpeg";
    public static final String MIME_AUDIO_MP4 = "audio/mp4";
    public static final String MIME_AUDIO_X_AIFF = "audio/x-aiff";
    public static final String MIME_AUDIO_X_MPEGURL = "audio/x-mpegurl";
    public static final String MIME_AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio";
    public static final String MIME_AUDIO_X_WAV = "audio/x-wav";
    public static final String MIME_AUDIO_X_M4A = "audio/x-m4a";
    public static final String MIME_CHEMICAL_X_PDB = "chemical/x-pdb";
    public static final String MIME_CHEMICAL_X_XYZ = "chemical/x-xyz";
    public static final String MIME_MODEL_IGES = "model/iges";
    public static final String MIME_MODEL_MESH = "model/mesh";
    public static final String MIME_MODEL_VRLM = "model/vrml";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_RICHTEXT = "text/richtext";
    public static final String MIME_TEXT_RTF = "text/rtf";
    public static final String MIME_TEXT_HTML = "text/html";
    public static final String MIME_TEXT_CALENDAR = "text/calendar";
    public static final String MIME_TEXT_CSS = "text/css";
    public static final String MIME_TEXT_SGML = "text/sgml";
    public static final String MIME_TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values";
    public static final String MIME_TEXT_VND_WAP_XML = "text/vnd.wap.wml";
    public static final String MIME_TEXT_VND_WAP_WMLSCRIPT = "text/vnd.wap.wmlscript";
    public static final String MIME_TEXT_X_SETEXT = "text/x-setext";
    public static final String MIME_TEXT_X_COMPONENT = "text/x-component";
    public static final String MIME_VIDEO_QUICKTIME = "video/quicktime";
    public static final String MIME_VIDEO_MPEG = "video/mpeg";
    public static final String MIME_VIDEO_VND_MPEGURL = "video/vnd.mpegurl";
    public static final String MIME_VIDEO_X_MSVIDEO = "video/x-msvideo";
    public static final String MIME_VIDEO_X_MS_WMV = "video/x-ms-wmv";
    public static final String MIME_VIDEO_X_SGI_MOVIE = "video/x-sgi-movie";
    public static final String MIME_X_CONFERENCE_X_COOLTALK = "x-conference/x-cooltalk";

    private static HashMap<String, String> mimeTypeMapping;

    static {
        mimeTypeMapping = new HashMap<String, String>(200) {
            {
                put(MIME_APPLICATION_VND_MOZZILLA_XUL_XML, "xul");
                put(MIME_APPLICATION_JSON, "json");
                put(MIME_X_CONFERENCE_X_COOLTALK, "ice");
                put(MIME_VIDEO_X_SGI_MOVIE, "movie");
                put(MIME_VIDEO_X_MSVIDEO, "avi");
                put(MIME_VIDEO_X_MS_WMV, "wmv");
                put(MIME_VIDEO_VND_MPEGURL, "m4u");
                put(MIME_TEXT_X_COMPONENT, "htc");
                put(MIME_TEXT_X_SETEXT, "etx");
                put(MIME_TEXT_VND_WAP_WMLSCRIPT, "wmls");
                put(MIME_TEXT_VND_WAP_XML, "wml");
                put(MIME_TEXT_TAB_SEPARATED_VALUES, "tsv");
                put(MIME_TEXT_SGML, "sgm");
                put(MIME_TEXT_CSS, "css");
                put(MIME_TEXT_CALENDAR, "ics");
                put(MIME_MODEL_VRLM, "vrlm");
                put(MIME_MODEL_MESH, "mesh");
                put(MIME_MODEL_IGES, "iges");
                put(MIME_IMAGE_X_RGB, "rgb");
                put(MIME_IMAGE_X_PORTABLE_PIXMAP, "ppm");
                put(MIME_IMAGE_X_PORTABLE_GRAYMAP, "pgm");
                put(MIME_IMAGE_X_PORTABLE_BITMAP, "pbm");
                put(MIME_IMAGE_X_PORTABLE_ANYMAP, "pnm");
                put(MIME_IMAGE_X_ICON, "ico");
                put(MIME_IMAGE_X_CMU_RASTER, "ras");
                put(MIME_IMAGE_WAP_WBMP, "wbmp");
                put(MIME_IMAGE_VND_DJVU, "djvu");
                put(MIME_IMAGE_SVG_XML, "svg");
                put(MIME_IMAGE_IEF, "ief");
                put(MIME_IMAGE_CGM, "cgm");
                put(MIME_IMAGE_BMP, "bmp");
                put(MIME_CHEMICAL_X_XYZ, "xyz");
                put(MIME_CHEMICAL_X_PDB, "pdb");
                put(MIME_AUDIO_X_PN_REALAUDIO, "ram");
                put(MIME_AUDIO_X_MPEGURL, "m3u");
                put(MIME_AUDIO_X_AIFF, "aiff");
                put(MIME_AUDIO_MPEG, "mp3");
                put(MIME_APPLICATION_XML_DTD, "dtd");
                put(MIME_APPLICATION_XML, "xml");
                put(MIME_APPLICATION_XSLT_XML, "xslt");
                put(MIME_APPLICATION_XHTML_XML, "xhtml");
                put(MIME_APPLICATION_X_WAIS_SOURCE, "src");
                put(MIME_APPLICATION_X_USTAR, "ustar");
                put(MIME_APPLICATION_X_TROFF_MS, "ms");
                put(MIME_APPLICATION_X_TROFF_ME, "me");
                put(MIME_APPLICATION_X_TROFF_MAN, "man");
                put(MIME_APPLICATION_X_TROFF, "roff");
                put(MIME_APPLICATION_X_TEXINFO, "texinfo");
                put(MIME_APPLICATION_X_TEX, "tex");
                put(MIME_APPLICATION_X_TCL, "tcl");
                put(MIME_APPLICATION_X_SV4CRC, "sv4crc");
                put(MIME_APPLICATION_X_SV4CPIO, "sv4cpio");
                put(MIME_APPLICATION_X_STUFFIT, "sit");
                put(MIME_APPLICATION_X_SHOCKWAVE_FLASH, "swf");
                put(MIME_APPLICATION_X_SHAR, "shar");
                put(MIME_APPLICATION_X_SH, "sh");
                put(MIME_APPLICATION_X_NETCDF, "nc");
                put(MIME_APPLICATION_X_LATEX, "latex");
                put(MIME_APPLICATION_X_KOAN, "skp");
                put(MIME_APPLICATION_X_JAVASCRIPT, "js");
                put(MIME_APPLICATION_X_HDF, "hdf");
                put(MIME_APPLICATION_X_GTAR, "gtar");
                put(MIME_APPLICATION_X_FUTURESPLASH, "spl");
                put(MIME_APPLICATION_X_DVI, "dvi");
                put(MIME_APPLICATION_X_DIRECTOR, "dcr");
                put(MIME_APPLICATION_X_CSH, "csh");
                put(MIME_APPLICATION_X_CPIO, "cpio");
                put(MIME_APPLICATION_X_CHESS_PGN, "pgn");
                put(MIME_APPLICATION_X_CDLINK, "vcd");
                put(MIME_APPLICATION_X_BCPIO, "bcpio");
                put(MIME_APPLICATION_VND_RNREALMEDIA, "rm");
                put(MIME_APPLICATION_VND_MSPOWERPOINT, "ppt");
                put(MIME_APPLICATION_VND_MIF, "mif");
                put(MIME_APPLICATION_SRGS_XML, "grxml");
                put(MIME_APPLICATION_SRGS, "gram");
                put(MIME_APPLICATION_RDF_SMIL, "smi");
                put(MIME_APPLICATION_RDF_XML, "rdf");
                put(MIME_APPLICATION_X_OGG, "ogg");
                put(MIME_APPLICATION_ODA, "oda");
                put(MIME_APPLICATION_OCTET_STREAM, "dmg");
                put(MIME_APPLICATION_MATHML_XML, "mathml");
                put(MIME_APPLICATION_MAC_COMPACTPRO, "cpt");
                put(MIME_APPLICATION_MAC_BINHEX40, "hqx");
                put(MIME_APPLICATION_JNLP, "jnlp");
                put(MIME_APPLICATION_ANDREW_INSET, "ez");
                put(MIME_TEXT_PLAIN, "txt");
                put(MIME_TEXT_RTF, "rtf");
                put(MIME_TEXT_RICHTEXT, "rtx");
                put(MIME_TEXT_HTML, "html");
                put(MIME_APPLICATION_ZIP, "zip");
                put(MIME_APPLICATION_X_RAR_COMPRESSED, "rar");
                put(MIME_APPLICATION_X_GZIP, "gzip");
                put(MIME_APPLICATION_TGZ, "tgz");
                put(MIME_APPLICATION_X_TAR, "tar");
                put(MIME_IMAGE_GIF, "gif");
                put(MIME_IMAGE_JPEG, "jpeg");
                put(MIME_IMAGE_TIFF, "tiff");
                put(MIME_IMAGE_PNG, "png");
                put(MIME_AUDIO_BASIC, "au");
                put(MIME_AUDIO_X_WAV, "wav");
                put(MIME_AUDIO_MP4, "m4a");
                put(MIME_AUDIO_X_M4A, "m4a");
                put(MIME_AUDIO_MIDI, "midi");
                put(MIME_VIDEO_QUICKTIME, "mov");
                put(MIME_VIDEO_MPEG, "mpeg");
                put(MIME_APPLICATION_MSWORD, "doc");
                put(MIME_APPLICATION_VND_MSEXCEL, "xls");
                put(MIME_APPLICATION_POSTSCRIPT, "ps");
                put(MIME_APPLICATION_PDF, "pdf");
                put(MIME_APPLICATION_JAVA_ARCHIVE, "jar");
            }};
    }

    /**
     * Returns the corresponding MIME type to the given extension.
     * If no MIME type was found it returns 'application/octet-stream' type.
     */
    public static String getFileExtention(String givenMimeType) {
        String extension = lookupMimeType(givenMimeType);
        if (extension == null) {
            throw new ExtensionNotFound("Unable to find extension for " + givenMimeType);
        }
        return extension;
    }

    /**
     * Simply returns File extension or <code>null</code> if no type is found.
     */
    public static String lookupMimeType(String mimeType) {
        return mimeTypeMapping.get(mimeType.toLowerCase());
    }
}
