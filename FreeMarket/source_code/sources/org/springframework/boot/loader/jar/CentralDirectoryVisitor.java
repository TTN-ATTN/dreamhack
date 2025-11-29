package org.springframework.boot.loader.jar;

import org.springframework.boot.loader.data.RandomAccessData;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/CentralDirectoryVisitor.class */
interface CentralDirectoryVisitor {
    void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

    void visitFileHeader(CentralDirectoryFileHeader fileHeader, long dataOffset);

    void visitEnd();
}
