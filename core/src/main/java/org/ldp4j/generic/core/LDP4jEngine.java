/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ldp4j.generic.core;

import java.util.List;

public class LDP4jEngine {

    private List<Handler> handlerChain;

    private int counter = 0;

    private String lastHandlerName;

    public void serve(LDPContext context) throws LDPFault {

        if (handlerChain == null) {
            throw new IllegalStateException("LDP4j engine is not correctly configured with a handler chain");
        }

        for (Handler handler : handlerChain) {
            HandlerResponse response = handler.invoke(context);
            lastHandlerName = handler.getName();
            counter++;
            if(HandlerResponse.ABORT.equals(response)){
                break;
            }
        }
    }

    public void resume(LDPContext context) throws LDPFault {

        List<Handler> remainingHandlerChain = handlerChain.subList(counter, handlerChain.size());

        for (Handler handler : remainingHandlerChain) {
            HandlerResponse response = handler.invoke(context);
            lastHandlerName = handler.getName();
            counter++;
            if(HandlerResponse.ABORT.equals(response)){
                break;
            }
        }


    }

    public void setHandlerChain(List<Handler> handlers){
        handlerChain = handlers;
    }

    public String getLastHandlerName(){
        return lastHandlerName;
    }

}
