package ru.bcc.airstage.server;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import ru.bcc.airstage.airplay.AirPlayGateway;
import ru.bcc.airstage.airplay.DeviceResponse;
import ru.bcc.airstage.airplay.command.PlayCommand;
import ru.bcc.airstage.database.ContentDao;
import ru.bcc.airstage.database.DeviceDao;
import ru.bcc.airstage.model.Content;
import ru.bcc.airstage.model.ContentFormat;
import ru.bcc.airstage.model.ContentSource;
import ru.bcc.airstage.model.Device;
import ru.bcc.airstage.server.command.Action;
import ru.bcc.airstage.server.command.Request;
import ru.bcc.airstage.server.command.Response;
import ru.bcc.airstage.stream.StreamServer;

import java.net.Inet4Address;
import java.util.Collections;
import java.util.HashMap;

/**
 * Date: 08.05.13
 * Time: 11:31
 *
 * @author Artem Prigoda
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ContentPlayerTest {

    @TestedObject
    ContentPlayer contentPlayer;

    @InjectIntoByType
    Mock<DeviceDao> deviceDaoMock;

    @InjectIntoByType
    Mock<ContentDao> contentDaoMock;

    @InjectIntoByType
    Mock<AirPlayGateway> airPlayGatewayMock;

    @InjectIntoByType
    Mock<StreamServer> streamServerMock;


    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayButContentNotExist() throws Exception {
        contentPlayer.playContent("18", "25");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayButDeviceNotExist() throws Exception {
        contentDaoMock.returns(new Content("18", "Walk from Colorado", "Set Jones",
                ContentFormat.MPEG_4, "url", false, ContentSource.ITUNES)).getById("18");

        contentPlayer.playContent("18", "25");
    }

    @Test
    public void testGetPlay() throws Exception {
        contentDaoMock.returns(new Content("18", "Walk from Colorado", "Set Jones",
                ContentFormat.MPEG_4, "url", false, ContentSource.ITUNES)).getById("18");
        Device device = new Device("25", "Home AppleTV", Inet4Address.getByName("192.168.52.11"), 7000);
        deviceDaoMock.returns(device).getById("25");

        String headers = "HTTP/1.1 200 OK\n" +
                "Date: Thu, 01 Jan 1970 00:10:32 GMT\n" +
                "Content-Length: 0";
        DeviceResponse deviceResponse = new DeviceResponse(headers, null);
        airPlayGatewayMock.returns(deviceResponse).sendCommand(new PlayCommand("http://192.168.52.248:8080/stream?code=18", 0.0), device);

        streamServerMock.returns("192.168.52.248").getHost();
        streamServerMock.returns(8080).getPort();

        String result = contentPlayer.playContent("18", "25");
        System.out.println(result);
        Assert.assertEquals(result, "200 OK");
    }


}
