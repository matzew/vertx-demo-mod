/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wessendorf.aerogear.vertx;

import org.jboss.aerogear.push.registration.DeviceRegistrationService;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class DeviceRegistrationModule extends BusModBase implements Handler<Message<JsonObject>> {

    private final DeviceRegistrationService drs = new DeviceRegistrationService();
    
    @Override
    public void start() {
        super.start();
        
        // we register out call on a namespace/channel;
        // incoming message are on handle();
        eb.registerHandler("org.aerogear.device.reg", this);
    }

    @Override
    public void stop() throws Exception {
        // do clean ups...

        super.stop();
    }

    @Override
    public void handle(Message<JsonObject> message) {
        
        String action = message.body.getString("action");
        if (action == null) {
            //sendError(message, "action must be specified");
            return;
        }
        
        // TODO: use switch....
        if (action.equals("save")) {
            storeDevice(message);
        } else {
            sendError(message, "Invalid action: " + action);
            return;
        }
    }

    private void storeDevice(Message<JsonObject> message) {
        System.out.println("storing the device....");
        JsonObject deviceInfo = getMandatoryObject("device", message);
        
        drs.registerDevice(deviceInfo.getString("token"), deviceInfo.getString("os"), "noop");
        
        JsonObject json = new JsonObject();
        json.putString("status", "OK");
        message.reply(json);
    }

}