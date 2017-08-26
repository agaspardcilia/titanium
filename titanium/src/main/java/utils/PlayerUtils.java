package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import screach.titanium.core.Player;

public class PlayerUtils {

	public static void updateVACBanStatus(Player p, int tries) {
		int i = 0;
		do {
			try {
				int vbs = getVacBans(p.getSteamId());
				p.setVacBans(vbs);
				return;
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
			i++;
		} while (i < tries);

		p.setVacBans(-1);
	}

	private static Node getSingleChildByName(Node n, String childName) {
		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(childName))
				return  children.item(i);
		}

		return null;
	}
	
	private static int getVacBans(String steamId) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse("http://steamcommunity.com/profiles/" + steamId + "/?xml=1");
	
		return Integer.parseInt(getSingleChildByName(getSingleChildByName(doc, "profile"), "vacBanned").getTextContent());
	}
	

}
