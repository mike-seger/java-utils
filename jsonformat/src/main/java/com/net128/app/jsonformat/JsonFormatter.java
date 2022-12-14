package com.net128.app.jsonformat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonFormatter {
	final static String newline = System.lineSeparator();
	final static Map<Integer, String> indents = new HashMap<>();

	public void writeFormattedJson(final InputStream is, final int indentWidth, final boolean compact, final OutputStream os) throws IOException {
		try (var bis = new BufferedInputStream(is)) {
			try (var out = new PrintWriter(new BufferedOutputStream(os))) {
				var inQuotes = false;
				int i;
				var indent = 0;
				var arrayDepth = 0;
				while ((i = bis.read()) != -1) {
					var c = (char) i;
					if (c == '\"') {
						out.write(c);
						inQuotes = !inQuotes;
						continue;
					}

					if (!inQuotes) {
						switch (c) {
							case '{':
							case '[':
								if(compact && c=='[')  {
									out.write(c);
									arrayDepth++;
								}
								else {
									out.write(c);
									if(arrayDepth<=1) out.write(indent(indent += indentWidth));
									else indent += indentWidth;
								}
								continue;
							case '}':
							case ']':
								if(compact && c==']') {
									out.write(c);
									arrayDepth--;
								}
								else {
									if(arrayDepth<=1) out.write(indent(indent -= indentWidth));
									else indent -= indentWidth;
									out.write(c);
								}
								continue;
							case ':':
								out.write(c);
								out.write(' ');
								continue;
							case ',':
								if(compact && arrayDepth>0) out.write(c);
								else {
									out.write(c);
									out.write(indent(indent));
								}
								continue;
							default:
								if (Character.isWhitespace(c)) continue;
						}
					}

					if(c == '\\') {
						out.write(c);
						out.write(bis.read());
					} else out.write(c);
				}
			}
		}
		System.err.println(indents);
	}

	private String indent(final int indent) {
		if(indent==0) return newline;
		return indents.compute(indent, (key, val) ->  newline+" ".repeat(indent));
	}

	public static void main(String[] args) throws IOException {
		new JsonFormatter().run(args);
	}

	private void run(String[] theArgs) throws IOException {
		var arguments = new ArrayList<>(Arrays.asList(theArgs));
		var compact = true;
		int pos = arguments.indexOf("-nc");
		if(pos>=0) { arguments.remove(pos); compact = false; }
		InputStream is;
		if(arguments.size() > 0) {
			if(arguments.size()>2 || !arguments.get(0).matches("(-f| *[\"{].* *[\"}] *)")) usage();
			if(arguments.size()==1) is=new ByteArrayInputStream(arguments.get(0).getBytes(StandardCharsets.UTF_8));
			else is = new FileInputStream(arguments.get(1));
		} else {
			if (System.in.available() == 0) {
				usage();
				return;
			}
			is = System.in;
		}
		try (var bis = new BufferedInputStream(is)) {
			writeFormattedJson(bis, 2, compact, System.out);
		}
	}

	private void usage() {
		System.out.printf("Usage: %s [-nc|--not-compact] [-f <json-file>|<json-string>]\n\tAlternatively STDIN may contain JSON", getClass().getSimpleName());
		System.exit(1);
	}
}
