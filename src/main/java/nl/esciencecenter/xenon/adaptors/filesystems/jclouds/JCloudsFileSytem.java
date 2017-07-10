package nl.esciencecenter.xenon.adaptors.filesystems.jclouds;

import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.adaptors.NotConnectedException;
import nl.esciencecenter.xenon.adaptors.XenonProperties;
import nl.esciencecenter.xenon.filesystems.*;
import org.apache.commons.io.input.NullInputStream;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.*;
import org.jclouds.blobstore.options.CopyOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// We emulate the presence of (empty) directories by a file ending in a /
// this is the same behaviour as the official S3 console

public class JCloudsFileSytem extends FileSystem {

    final String bucket;
    final BlobStoreContext context;
    final String adaptorName;
    final String endPoint;
    boolean open ;

    public JCloudsFileSytem(String uniqueID,String adaptorName, String endPoint, BlobStoreContext context, String bucket, XenonProperties properties) {
        super(uniqueID,adaptorName,endPoint,new Path(""),properties);
        this.context = context;
        this.bucket = bucket;
        this.adaptorName = adaptorName;
        this.open = true;
        this.endPoint = endPoint;
    }

    @Override
    public void close() throws XenonException {
        context.close();
        open = false;
    }

    @Override
    public boolean isOpen() throws XenonException {
        return open;
    }

    void checkClosed() throws XenonException{
        if(!isOpen()){
            throw new NotConnectedException(getAdaptorName(), "Already closed file system!");
        }
    }

/*
    @Override
    protected void copyFile(Path source, FileSystem destinationFS, Path destination, CopyMode mode) throws XenonException{

        if(destinationFS instanceof  JCloudsFileSytem){
            JCloudsFileSytem dest = (JCloudsFileSytem)destinationFS;
            if( dest.endPoint.equals(endPoint) && dest.adaptorName.equals(adaptorName)){
                if (exists(destination)) {
                    switch (mode) {
                        case CREATE:
                            throw new PathAlreadyExistsException(getAdaptorName(), "Destination path already exists: " + destination);
                        case IGNORE:
                            return;
                        case REPLACE:
                            // continue
                            break;
                    }
                }
                context.getBlobStore().copyBlob(bucket,source.getRelativePath(),
                        dest.bucket,destination.getRelativePath(),CopyOptions.NONE);
                return;
            }
        }
        // revert to default copy
        super.copyFile(source,destinationFS,destination,mode);
    }
*/

    @Override
    public void createDirectory(Path dir) throws XenonException {
        checkClosed();
        if(exists(dir)){
            throw new PathAlreadyExistsException(adaptorName, "Cannot create directory, path already exists : " + dir.getRelativePath());
        }
        if(!exists(dir.getParent())){
            throw new NoSuchPathException(adaptorName, "Cannot create file, " + dir.getRelativePath() + ", parent directory " + dir.getParent().getRelativePath() + "does not exists.");
        }

        String path = dir.getRelativePath();
        if(!path.endsWith("/")){
            path = path + "/";
        }
        InputStream emtpy = new org.apache.sshd.common.util.io.NullInputStream();
        final Blob b = context.getBlobStore().blobBuilder(bucket).name(path).payload(new org.apache.sshd.common.util.io.NullInputStream()).contentLength(0).build();
        context.getBlobStore().putBlob(bucket,b);
    }

    @Override
    public void createFile(Path file) throws XenonException {
        checkClosed();
        if(exists(file)){
            throw new PathAlreadyExistsException(adaptorName, "Cannot create file, path already exists : " + file.getRelativePath());
        }
        if(!exists(file.getParent())){
            throw new NoSuchPathException(adaptorName, "Cannot create file, " + file.getRelativePath() + ", parent directory " + file.getParent().getRelativePath() + "does not exists.");
        }
        String path = file.getRelativePath();
        InputStream emtpy = new org.apache.sshd.common.util.io.NullInputStream();
        final Blob b = context.getBlobStore().blobBuilder(bucket).name(path).payload(new org.apache.sshd.common.util.io.NullInputStream()).contentLength(0).build();
        context.getBlobStore().putBlob(bucket,b);
    }

    @Override
    public void createSymbolicLink(Path link, Path target) throws XenonException {
        throw new AttributeNotSupportedException(adaptorName, "Symbolic link  not supported by " + adaptorName);
    }

    private void delete(String path){
        boolean exists = context.getBlobStore().blobExists(bucket,path);
        if(exists){
            context.getBlobStore().removeBlob(bucket,path);
        }
    }

    @Override
    public void deleteFile(Path file) throws XenonException{
        checkClosed();
        delete(file.getRelativePath());
    }

    @Override
    protected void deleteDirectory(Path dir) throws XenonException {
        checkClosed();
        String path = dir.getRelativePath();
        if(!path.endsWith("/")){
            path = path + "/";
        }
        delete(path);
    }

    @Override
    protected Iterable<PathAttributes> listDirectory(Path dir) throws XenonException {
        return list(dir, false);
    }


    @Override
    public boolean exists(Path path) throws XenonException {
        checkClosed();
        String name = path.getRelativePath();
        boolean exists = context.getBlobStore().blobExists(bucket,name);
        if(!exists && !path.endsWith("/")){
            return context.getBlobStore().blobExists(bucket,name +"/");
        }
        return exists;
    }


    PathAttributes toPathAttributes(final StorageMetadata m, final BlobAccess access){
        Path p = new Path(m.getName());

        PathAttributes pa = new PathAttributes();
        pa.setPath(p);
        pa.setCreationTime(m.getCreationDate().getTime());
        pa.setLastModifiedTime(m.getLastModified().getTime());
        pa.setReadable(true);
        boolean isDir = m.getName().endsWith("/");
        pa.setExecutable(isDir);
        pa.setWritable(true);
        pa.setDirectory(m.getName().endsWith("/"));
        Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        if(access == BlobAccess.PUBLIC_READ){
            permissions.add(PosixFilePermission.OTHERS_READ);
            if(isDir) { permissions.add(PosixFilePermission.OTHERS_EXECUTE); }
        }
        pa.setPermissions(permissions);
        return pa;
    }

    class ListingIterator implements Iterator<PathAttributes> {

        private final ListContainerOptions options;
        Iterator<? extends StorageMetadata> curIterator;
        PageSet<? extends StorageMetadata> curPageSet;
        StorageMetadata nxt;

        ListingIterator(ListContainerOptions options, PageSet<? extends StorageMetadata> pageSet){
            this.options = options;
            this.curPageSet = pageSet;
            this.curIterator = curPageSet.iterator();
            getNext();
        }

        void getNext(){
            if (!curIterator.hasNext() && curPageSet.getNextMarker() != null){
                curPageSet = context.getBlobStore().list(bucket,options.afterMarker(curPageSet.getNextMarker()));
                curIterator = curPageSet.iterator();
            }
            if(curIterator.hasNext()){
                nxt = curIterator.next();
            } else {
                nxt = null;
            }
        }

        @Override
        public boolean hasNext() { return  nxt != null; }

        @Override
        public PathAttributes next() {
            BlobAccess acess = context.getBlobStore().getBlobAccess(bucket,nxt.getName());
            PathAttributes res = toPathAttributes(nxt,acess);
            getNext();
            return res;
        }
    }


    public Iterable<PathAttributes> list(Path dir, boolean recursive) throws XenonException{
        checkClosed();
        String name = dir.getRelativePath();
        if(!name.endsWith("/")){
            if(!context.getBlobStore().blobExists(bucket,name + "/")){
                if(context.getBlobStore().blobExists(bucket,name )){
                    throw new InvalidPathException(adaptorName, "Not a directory : " + dir.getRelativePath());
                } else {
                    throw new NoSuchPathException(adaptorName, " No such file or directory: " + dir.getRelativePath());
                }
            }
        } else {
            if(!context.getBlobStore().blobExists(bucket,name )){
                throw new NoSuchPathException(adaptorName, " No such file or directory: " + dir.getRelativePath());
            }
        }

        if(!exists(dir)){
            throw new NoSuchPathException(adaptorName, "Cannot list " + dir.getRelativePath() + ": no such file or directory");
        }
        ListContainerOptions options = new ListContainerOptions().prefix(dir.getRelativePath());
        if(recursive) { options = options.recursive(); }
        final ListContainerOptions optionsFinal = options;

        return new Iterable<PathAttributes>() {
            @Override
            public Iterator<PathAttributes> iterator() {
                return new ListingIterator(optionsFinal,context.getBlobStore().list(bucket,optionsFinal));
            }
        };

    }

    @Override
    public InputStream readFromFile(Path path) throws XenonException {
        String name = path.getRelativePath();
        boolean exists = context.getBlobStore().blobExists(bucket,name);
        if(exists) {
            Blob b = context.getBlobStore().getBlob(bucket, path.getRelativePath());
            try {
                return b.getPayload().openStream();
            } catch (IOException e) {
                throw new XenonException(adaptorName, e.getMessage());
            }
        } else {
            throw new NoSuchPathException(adaptorName,"No such file: " + name);
        }
    }

    @Override
    public OutputStream writeToFile(Path path, long size) throws XenonException {
        final PipedInputStream read = new PipedInputStream();
        final Blob b = context.getBlobStore().blobBuilder(bucket).name(path.getRelativePath()).payload(read).contentLength(size).build();
        try {
            final OutputStream out = new PipedOutputStream(read);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    context.getBlobStore().putBlob(bucket,b);
                }
            }).start();
            return out;
        } catch(IOException e){
            throw new XenonException(adaptorName,"IO error when trying to write: " + e.getMessage());
        }
    }

    @Override
    public OutputStream writeToFile(Path file) throws XenonException {
        throw new XenonException(adaptorName, "Sorry, this adaptor needs to know the size of a file before we start writing.");
    }

    @Override
    public OutputStream appendToFile(Path file) throws XenonException {
        throw new XenonException(adaptorName, "Sorry, this adaptor does not support appending.");
    }




    @Override
    public PathAttributes getAttributes(Path path) throws XenonException {
        if(exists(path)){
            throw new NoSuchPathException(adaptorName, "File does not exist: " + path.getRelativePath());
        }
        BlobMetadata md = context.getBlobStore().blobMetadata(bucket,path.getRelativePath());
        BlobAccess access = context.getBlobStore().getBlobAccess(bucket, path.getRelativePath());
        return toPathAttributes(md,access);
    }

    @Override
    public Path readSymbolicLink(Path link) throws XenonException {
        throw new AttributeNotSupportedException(adaptorName, "Symbolic link  not supported by " + adaptorName);
    }

    @Override
    public void setPosixFilePermissions(Path path, Set<PosixFilePermission> permissions) throws XenonException {
        String s = path.getRelativePath();
        boolean isDir = s.endsWith("/");
        if(!isDir){
            if(!context.getBlobStore().blobExists(bucket,s ) &&
                    context.getBlobStore().blobExists(bucket,s +"/")){
                isDir = true;
                s += "/";
            }
        }
        boolean publicAccess = false;
        for(PosixFilePermission p : permissions){
            switch(p){
                case OTHERS_READ: publicAccess = true; break;
                default : break;
            }
        }
        BlobAccess ba = publicAccess ? BlobAccess.PUBLIC_READ : BlobAccess.PRIVATE;
        context.getBlobStore().setBlobAccess(bucket,s,ba);
    }





}
