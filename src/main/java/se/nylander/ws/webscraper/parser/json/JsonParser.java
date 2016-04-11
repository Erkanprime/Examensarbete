package se.nylander.ws.webscraper.parser.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import se.nylander.ws.webscraper.config.ScraperConstants;
import se.nylander.ws.webscraper.exception.JavascriptJsonFormatException;
import se.nylander.ws.webscraper.model.ItemSockets;
import se.nylander.ws.webscraper.model.Mods;
import se.nylander.ws.webscraper.model.TradeItem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by erik.nylander on 2016-02-18.
 */
public class JsonParser {

    public JsonParser(){}

    public Optional<List<TradeItem>> processJsonItemDataString(String dirtyJson) {
        String json;
        try {
            json = dirtyJson.substring(dirtyJson.indexOf("[["), dirtyJson.indexOf(")).run"));
        } catch (IndexOutOfBoundsException e) {
            throw new JavascriptJsonFormatException(" Failed to parse Javascript Json Body ", e);
        }
        JSONArray arr = new JSONArray(json);
        List<String> items = new ArrayList<>();
        String cleanJson = "";

        for (int i = 0; i < arr.length(); i++) {

            // Ta bort dom första hak-lådorna
            try {
                String base = arr.get(i).toString();

                cleanJson = base.substring(base.indexOf("{"), base.lastIndexOf(",[]]"));

            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            items.add(cleanJson);
        }

        return jsonParsing(items);
    }

    private static Optional<List<TradeItem>> jsonParsing(List<String> cleanJson) {
        List<TradeItem> tradeItems = new ArrayList<>();

        cleanJson.stream().forEach(item -> {
            TradeItem tradeItem = new TradeItem();

            JSONObject object = new JSONObject(item);

            tradeItem.setCorrupted(object.getBoolean(ScraperConstants.ITEM_CORRUPTED));
            tradeItem.setItemType(parseTypeLine(object));
            tradeItem.setIdentified(object.getBoolean(ScraperConstants.ITEM_IDENTIFIED));
            tradeItem.setIcon(object.getString(ScraperConstants.ITEM_ICON));
            tradeItem.setLeague(object.getString(ScraperConstants.ITEM_LEAGUE));
            tradeItem.setName(parseName(object));
            tradeItem.setVerified(object.getBoolean(ScraperConstants.ITEM_VERIFIED));
            tradeItem = parseProperties(object, tradeItem);
            tradeItem = parseMods(object, tradeItem);

            tradeItem = parseLevelRequirment(object, tradeItem);
            tradeItem = parseSockets(object, tradeItem);

            tradeItems.add(tradeItem);

        });
        return Optional.of(tradeItems);
    }

    // Check if typeline contains >>
    private static String parseTypeLine(JSONObject object) {
        try {
            String typeLine = object.getString(ScraperConstants.ITEM_TYPE);
            return typeLine.contains(">>") ? typeLine.substring(typeLine.lastIndexOf(">>") + 2) : typeLine;
        } catch (JSONException e) {
            return ScraperConstants.ITEM_UNDEFINED;
        }
    }

    // Check if name is empty or contains >>, if yes it returns typeline
    private static String parseName(JSONObject object) {
        String name;
        try {
            name = object.getString(ScraperConstants.ITEM_NAME);
            return name.contains(">>") ? name.substring(name.lastIndexOf(">>") + 2) : name;

        } catch (JSONException e) {
            name = parseTypeLine(object);
        }
        return name;
    }

    private static TradeItem parseMods(JSONObject object, TradeItem tradeItem) {

            Optional<JSONArray> implicitMods = Optional.ofNullable(object.optJSONArray(ScraperConstants.ITEM_IMPLICIT_MODS));
            Optional<JSONArray> explicitMods = Optional.ofNullable(object.optJSONArray(ScraperConstants.ITEM_EXPLICIT_MODS));
            Optional<JSONArray> craftedMods = Optional.ofNullable(object.optJSONArray(ScraperConstants.ITEM_CRAFTED_MODS));


            if (implicitMods.isPresent()) {

                tradeItem = manageModProperties(implicitMods.get(), tradeItem);
            }
            if (explicitMods.isPresent()) {

                tradeItem = manageModProperties(explicitMods.get(), tradeItem);

            }if (craftedMods.isPresent()) {

                tradeItem = manageModProperties(craftedMods.get(), tradeItem);
            }

        return tradeItem;
    }

    private static TradeItem parseProperties(JSONObject object, TradeItem tradeItem) {
        try {
            JSONArray properties = object.getJSONArray(ScraperConstants.ITEM_PROPERTIES);

            for (int i = 0; i < properties.length(); i++) {

                if (!properties.getJSONObject(i).getJSONArray("values").isNull(0)) {
                    String value = properties.getJSONObject(i).getJSONArray("values").getJSONArray(0).toString();
                    String property = properties.getJSONObject(i).getString("name");
                    tradeItem.setBaseProperties(property, value);
                }
            }
        } catch (JSONException e) {
            return tradeItem;
        }
        return tradeItem;
    }

    private static TradeItem parseLevelRequirment(JSONObject object, TradeItem tradeItem) {
        try {
            JSONArray requirements = object.getJSONArray(ScraperConstants.ITEM_LEVEL_REQUIRMENT);

            for (int i = 0; i < requirements.length(); i++) {

                final String nameValue = requirements.getJSONObject(i).getString("name");
                if (nameValue.equals("Level")) {
                    Integer levelReq = requirements.getJSONObject(i).getJSONArray("values").getJSONArray(0).getInt(0);
                    tradeItem.setLevelRequirment(levelReq);
                    break;
                }

            }
        } catch (JSONException e) {
            return tradeItem;
        }
        return tradeItem;

    }

    private static TradeItem parseSockets(JSONObject object, TradeItem tradeItem) {
        try {
            JSONArray sockets = object.getJSONArray(ScraperConstants.ITEM_SOCKETS);
            Set<ItemSockets> socketsAndLinks = new HashSet<>();

            for (int i = 0; i < sockets.length(); i++) {
                JSONObject socket = sockets.getJSONObject(i);
                //TODO: Struntar i socket färg för tillfället
                Integer socketGroup = socket.getInt("group");
                tradeItem.addItemSocket(socketGroup, "");

            }
        } catch (JSONException e) {
            return tradeItem;
        }
        return tradeItem;
    }

    private static TradeItem manageModProperties(JSONArray mods , TradeItem tradeItem) {
        Set<Mods> modsList = new HashSet<>();

        for (int i = 0; i < mods.length(); i++) {

            String modName = mods.getString(i);
            Pattern pattern = Pattern.compile(ScraperConstants.MOD_REGEX);
            Matcher matcher = pattern.matcher(modName);


            Optional<Integer> optionalMinValue = Optional.ofNullable(matcher.find()
                    ? Integer.parseInt(modName.substring(matcher.start(), matcher.end()))
                    : null
            );

            Optional<Integer> optionalMaxValue = Optional.ofNullable(matcher.find()
                    ? Integer.parseInt(modName.substring(matcher.start(), matcher.end()))
                    : optionalMinValue.isPresent()
                                      ? optionalMinValue.get()
                                      : null
            );

            //Ta bort nummer för att göra mod-namnet generiskt
            modName = modName.replaceAll("(\\d+)","#");
            final Mods currentMod = new Mods(modName, optionalMinValue.isPresent()
                                            ? optionalMinValue.get()
                                            : null
                                        , optionalMaxValue.isPresent()
                                            ? optionalMaxValue.get()
                                            : null);

            Optional<Mods> existingMod = modsList.stream()
                                                 .filter(mod -> mod.equals(currentMod))
                                                 .findFirst();


            if(existingMod.isPresent()) {
                Integer existingModMinValue = existingMod.get().getMinValue();
                Integer existingModMaxValue = existingMod.get().getMaxValue();

                if(existingModMinValue != null && currentMod.getMinValue() != null) {
                    existingMod.get().setMinValue(existingModMinValue + currentMod.getMinValue());
                }
                if(existingModMaxValue != null && currentMod.getMaxValue() != null){
                    existingMod.get().setMaxValue(existingModMaxValue + currentMod.getMaxValue());
                }

                modsList.add(existingMod.get());
            } else {
                modsList.add(currentMod);
            }

        }
        tradeItem.setMods(modsList);
        return tradeItem;
    }

}
