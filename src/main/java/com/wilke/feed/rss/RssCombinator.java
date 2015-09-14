package com.wilke.feed.rss;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.wilke.feed.FeedAggregate;
import com.wilke.feed.rss.RssFeed.RssChannel;
import com.wilke.feed.rss.RssFeed.RssItem;

public class RssCombinator {

	private static final XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

	public static void aggregateFeed(final OutputStream out, final FeedAggregate aggregate) {
		try {
			final XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);

			writer.writeStartDocument();
			writer.writeStartElement(RssFeed.RSS);
			writer.writeAttribute(RssFeed.VERSION, "2.0");
			writer.writeNamespace("atom", "http://www.w3.org/2005/Atom"); // interoperability

			writer.writeStartElement(RssChannel.CHANNEL);

			writer.writeStartElement(RssChannel.TITLE);
			writer.writeCharacters(aggregate.title);
			writer.writeEndElement();

			writer.writeStartElement(RssChannel.LINK);
			writer.writeCharacters(aggregate.link);
			writer.writeEndElement();
			writer.writeEmptyElement("atom", RssChannel.LINK, "http://www.w3.org/2005/Atom"); // interoperability
			writer.writeAttribute("href", aggregate.link);
			writer.writeAttribute("rel", "self");
			writer.writeAttribute("type", "application/rss+xml");

			writer.writeStartElement(RssChannel.DESCRIPTION);
			writer.writeCharacters(aggregate.description);
			writer.writeEndElement();

			for (final Iterator<RssFeed> it = aggregate.fetchFeeds(); it.hasNext();) {
				final RssFeed feed = it.next();

				// TODO support multiple channels
				for (final RssItem item : feed.channel.items) {
					writer.writeStartElement(RssItem.ITEM);

					writer.writeStartElement(RssItem.TITLE);
					writer.writeCharacters(item.title);
					writer.writeEndElement();

					writer.writeStartElement(RssItem.CATEGORY);
					writer.writeCharacters(item.category);
					writer.writeEndElement();

					writer.writeStartElement(RssItem.LINK);
					writer.writeCharacters(item.link);
					writer.writeEndElement();

					writer.writeStartElement(RssItem.GUID);
					writer.writeCharacters(item.guid);
					writer.writeEndElement();

					writer.writeStartElement(RssItem.DESCRIPTION);
					writer.writeCData("<a href=\"" + item.link + "\" />" + item.title + "</a>");
					writer.writeEndElement();

					writer.writeStartElement(RssItem.PUBDATE);
					writer.writeCharacters(item.pubDate);
					writer.writeEndElement();

					writer.writeEndElement();
				}
			}

			writer.writeEndDocument(); // closing of open tags is automagically done
			writer.flush(); // always flush internal stream buffer
		} catch (final XMLStreamException e) {
			e.printStackTrace();
		}
	}
}