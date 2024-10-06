package prettifier;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import zeggiotti.univrprettycalendar.Main;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Prettifier {

    private final String APP_PASS, EMAIL, USER_ID;

    private final String EVENT_NAME_TAG = "SUMMARY:";
    private final String UID_TAG = "UID:";

    private final Map<Event, Event> events = new HashMap<Event, Event>();

    private final File calendar;
    private boolean writeLocation = true;

    public Prettifier(File calendar) {
        this.calendar = calendar;

        Properties properties = new Properties();
        try (InputStream input = Main.class.getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        APP_PASS = properties.getProperty("GMAIL_PROGRAM_PASSWORD");
        EMAIL = properties.getProperty("GMAIL_ADDRESS");
        USER_ID = properties.getProperty("GMAIL_USER_ID");

        scrapeEvents();
    }

    public Set<Event> getOldEvents() {
        return events.keySet();
    }

    public void writeLocation(boolean writeLocation) {
        this.writeLocation = writeLocation;
    }

    public void setBinding(Event oldEvent, Event newEvent) {
        events.put(oldEvent, newEvent);
    }

    public void removeEvent(Event oldEvent) {
        events.remove(oldEvent);
    }

    public void save(File outputFile) {
        try(BufferedReader reader = new BufferedReader(new FileReader(this.calendar));
            PrintWriter writer = new PrintWriter(outputFile)){

            String line;
            while((line = reader.readLine()) != null){
                if(line.contains("BEGIN:VEVENT")){
                    String msg = "";
                    String eventName, location = "";
                    Event thisEvent = null;
                    while(true) {
                        line = reader.readLine();

                        if(line.contains(EVENT_NAME_TAG)){
                            eventName = line.substring(EVENT_NAME_TAG.length());
                            for(Event event : events.keySet()){
                                if(event.getName().equals(eventName)){
                                    eventName = events.get(event).getName();
                                    location = events.get(event).getLocation();
                                    thisEvent = event;
                                }
                            }
                            line = EVENT_NAME_TAG + eventName;
                        }

                        msg += line + "\n";

                        if(line.contains("END:VEVENT")) {
                            if(writeLocation)
                                msg = "BEGIN:VEVENT\nLOCATION:" + location + "\n" +  msg;
                            if(thisEvent != null)
                                writer.print(msg);
                            break;
                        }
                    }
                } else writer.println(line);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void scrapeEvents() {
        try (BufferedReader reader = new BufferedReader(new FileReader(calendar))) {

            String line;
            Event event = null;
            while( (line = reader.readLine()) != null ) {
                if(line.contains("BEGIN:VEVENT"))
                    event = new Event("", "");

                if(line.contains(EVENT_NAME_TAG)){
                    event.setName(line.substring(EVENT_NAME_TAG.length()));
                }

                if(line.contains(UID_TAG)){
                    String location = line.substring(UID_TAG.length() + 10);
                    location = location.substring(0, location.indexOf(']') + 1);
                    location = addSpaces(location);
                    event.setLocation(location);
                }

                if(line.contains("END:VEVENT")){
                    events.putIfAbsent(event, null);
                    event = null;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String addSpaces(String s){
        for(int i = 1; i < s.length(); i++){
            if((Character.isUpperCase(s.charAt(i)) && s.charAt(i - 1) != '[') || s.charAt(i) == '-' || s.charAt(i) == '[' || Character.isDigit(s.charAt(i))){
                s = s.substring(0, i) + " " + s.substring(i);
                i++;
            }
        }
        return s;
    }

    public void sendCalendarViaEmail(String address, File calendar) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_ID, APP_PASS);
            }
        });

        String to = address;
        String from = EMAIL;
        String subject = "Calendario delle tue lezioni ad UniVR.";
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
            mimeBodyPart2.setText("Ecco il calendario .ics delle tue lezioni." +
                    " Si consiglia nel caso si voglia importare il calendario in un dispositivo Apple " +
                    "di aprire la mail con l'app Mail di sistema.\n\nGegios");
            multipart.addBodyPart(mimeBodyPart2);

            DataSource dataSource = new FileDataSource(calendar);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();

            mimeBodyPart.setDataHandler(new DataHandler(dataSource));
            mimeBodyPart.setFileName("calendario.ics");
            multipart.addBodyPart(mimeBodyPart);

            msg.setContent(multipart);

            // Send the message.
            Transport.send(msg);
        } catch (MessagingException e) {
            // Error.
        }
    }

}
