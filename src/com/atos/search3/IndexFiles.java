package com.atos.search3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import com.atos.search3.view.*;
import com.sun.beans.decoder.DocumentHandler;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing. Run
 * it with no command-line arguments for usage information.
 */

public class IndexFiles implements Runnable {
	static int i = 0;
	static String docsPath;
	private int var;
	public static SearchController search = new SearchController();

	// public static
	static IndexFiles index = new IndexFiles(10);
	static Thread t = new Thread();
	private MainApp mainApp;

	public IndexFiles(int var) {
		this.var = var;
	}

	/** Index all text files under a directory. */

	public static void index() {
		// String usage = "java org.apache.lucene.demo.IndexFiles"
		// + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
		// + "This indexes the documents in DOCS_PATH, creating a Lucene index"
		// + "in INDEX_PATH that can be searched with SearchFiles";

		String indexPath = "index";

		System.out.println(docsPath);

		if (docsPath.startsWith("www")) {
			Document doc = new Document();
			final Path file = Paths.get(docsPath);
			long lastModified = 0;
			

			try {
				
				Analyzer analyzer = new StandardAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				Directory dir = FSDirectory.open(Paths.get(indexPath));
				IndexWriter writer = new IndexWriter(dir, iwc);
				InputStream input = new URL("http://" + docsPath).openStream();
				doc = getDocument(input, file, lastModified);

				if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					// New index, so we just add the document (no old document
					// can
					// be there):
					System.out.println("adding " + file);
					writer.addDocument(doc);
				} else {
					// Existing index (an old copy of this document may have
					// been
					// indexed) so
					// we use updateDocument instead to replace the old one
					// matching
					// the exact
					// path, if present:
					System.out.println("updating " + file);
					writer.updateDocument(new Term("path", file.toString()), doc);
					writer.close();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {

			boolean create = true;
			// for(int i=0;i<args.length;i++) {
			// if ("-index".equals(args[i])) {
			// indexPath = args[i+1];
			// i++;
			// } else if ("-docs".equals(args[i])) {
			// docsPath = args[i+1];
			// i++;
			// } else if ("-update".equals(args[i])) {
			// create = false;
			// }
			// }

			// if (docsPath == null) {
			// System.err.println("Usage: " + usage);
			// System.exit(1);
			// }

			final Path docDir = Paths.get(docsPath);
			if (!Files.isReadable(docDir)) {
				System.out.println("Document directory '" + docDir.toAbsolutePath()
						+ "' does not exist or is not readable, please check the path");
				System.exit(1);
			}

			Date start = new Date();
			try {
				System.out.println("Indexing to directory '" + indexPath + "'...");

				Directory dir = FSDirectory.open(Paths.get(indexPath));
				Analyzer analyzer = new StandardAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

				if (create) {
					// Create a new index in the directory, removing any
					// previously indexed documents:
					iwc.setOpenMode(OpenMode.CREATE);
				} else {
					// Add new documents to an existing index:
					iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				}

				// Optional: for better indexing performance, if you
				// are indexing many documents, increase the RAM
				// buffer. But if you do this, increase the max heap
				// size to the JVM (eg add -Xmx512m or -Xmx1g):
				//
				// iwc.setRAMBufferSizeMB(256.0);

				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);

				// NOTE: if you want to maximize search performance,
				// you can optionally call forceMerge here. This can be
				// a terribly costly operation, so generally it's only
				// worth it when your index is relatively static (ie
				// you're done adding documents to it):
				//
				// writer.forceMerge(1);

				writer.close();

				Date end = new Date();
				System.out.println(end.getTime() - start.getTime() + " total milliseconds");

			} catch (IOException e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());

			}
		}
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param path
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @throws IOException
	 *             If there is a low-level I/O error
	 */
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		Document doc = new Document();

		System.out.println();
		System.out.println(i++);
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document

			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);

			File fille = new File(file.toString());

			String extension = "";

			int i = fille.getName().lastIndexOf('.');
			if (i > 0) {
				extension = fille.getName().substring(i + 1);
			}

			if (extension.equals("html")) {

				InputStream in = new FileInputStream(fille);
				doc = getDocument(in, file, lastModified);
			} else {
				// Add the contents of the file to a field named "contents".
				// Specify
				// a Reader,
				// so that the text of the file is tokenized and indexed, but
				// not
				// stored.
				// Note that FileReader expects the file to be in UTF-8
				// encoding.
				// If that's not the case searching for special characters will
				// fail.

				doc.add(pathField);

				// Add the last modified date of the file a field named
				// "modified".
				// Use a LongField that is indexed (i.e. efficiently filterable
				// with
				// NumericRangeFilter). This indexes to milli-second resolution,
				// which
				// is often too fine. You could instead create a number based on
				// year/month/day/hour/minutes/seconds, down the resolution you
				// require.
				// For example the long value 2011021714 would mean
				// February 17, 2011, 2-3 PM.
				doc.add(new LongField("modified", lastModified, Field.Store.NO));
				doc.add(new TextField("contents",
						new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			}
		}

		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			// New index, so we just add the document (no old document can
			// be there):
			System.out.println("adding " + file);
			writer.addDocument(doc);
		} else {
			// Existing index (an old copy of this document may have been
			// indexed) so
			// we use updateDocument instead to replace the old one matching
			// the exact
			// path, if present:
			System.out.println("updating " + file);
			writer.updateDocument(new Term("path", file.toString()), doc);
		}
	}

	@SuppressWarnings("deprecation")
	public static Document getDocument(InputStream is, Path file, long last) {
		Document doc = new Document();
		Field pathField = new StringField("path", file.toString(), Field.Store.YES);
		System.out.println("somtu");
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		org.w3c.dom.Document root = tidy.parseDOM(is, null);
		Element rawDoc = root.getDocumentElement();

		String body = getBody(rawDoc);

		if ((body != null) && (!body.equals(""))) {
			doc.add(pathField);

			doc.add(new Field("contents", body, Field.Store.NO, Field.Index.ANALYZED));
		}

		return doc;
	}

	protected String getTitle(Element rawDoc) {
		if (rawDoc == null) {
			return null;
		}

		String title = "";

		NodeList children = rawDoc.getElementsByTagName("title");
		if (children.getLength() > 0) {
			Element titleElement = ((Element) children.item(0));
			Text text = (Text) titleElement.getFirstChild();
			if (text != null) {
				title = text.getData();
			}
		}
		return title;
	}

	protected static String getBody(Element rawDoc) {
		if (rawDoc == null) {
			return null;
		}

		String body = "";
		NodeList children = rawDoc.getElementsByTagName("body");
		if (children.getLength() > 0) {
			body = getText(children.item(0));
		}
		return body;
	}

	protected static String getText(Node node) {
		NodeList children = node.getChildNodes();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			switch (child.getNodeType()) {
			case Node.ELEMENT_NODE:
				sb.append(getText(child));
				sb.append(" ");
				break;
			case Node.TEXT_NODE:
				sb.append(((Text) child).getData());
				break;
			}
		}
		return sb.toString();
	}

	@Override
	public void run() {

		index();

		// mainApp.warning();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				showDialog();
			}
		});
	}

	public void showDialog() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Oznámenie");
		alert.setHeaderText("Oznámenie");
		String s = "Indexovanie dokončené";
		alert.setContentText(s);
		alert.show();

	}

	public static void start(String path) {
		docsPath = path;

		(new Thread(new IndexFiles(10))).start();
	}
}
