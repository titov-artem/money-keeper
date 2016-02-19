package com.github.money.keeper.util.io;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestingInputStream extends InputStream {

    private final InputStream source;
    private final MessageDigest messageDigest;
    private byte[] digest;

    public DigestingInputStream(InputStream source) {
        this.source = source;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm for digesting found!");
        }
    }

    @Override
    public int read() throws IOException {
        int read = source.read();
        if (read == -1) {
            digest = messageDigest.digest();
        } else {
            messageDigest.update((byte) read);
        }
        return read;
    }

    public byte[] getRawDigest() {
        return digest;
    }

    public String getStringDigest() {
        return DatatypeConverter.printHexBinary(digest);
    }
}
