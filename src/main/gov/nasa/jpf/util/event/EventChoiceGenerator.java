/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package gov.nasa.jpf.util.event;

import gov.nasa.jpf.vm.ChoiceGeneratorBase;

/**
 * ChoiceGenerator for Events.
 * This is basically just a pointer into the event tree
 */
public class EventChoiceGenerator extends ChoiceGeneratorBase<Event> {

  protected Event base;
  protected Event cur;
  protected int nProcessed;
  
  public EventChoiceGenerator (String id, Event base){
    super(id);
    
    this.base = base;
  }
  
  public EventChoiceGenerator getSuccessor (String id){
    if (cur == null){
      return new EventChoiceGenerator(id, base.getNext());
      
    } else {
      Event next = cur.getNext();
      
      if (cur instanceof CheckEvent){ // CheckEvents use next for conjunction
        while (next instanceof CheckEvent){
          next = next.getNext();
        }
      }
      
      if (next != null){
        return new EventChoiceGenerator( id, next);
      } else {
        return null; // done
      }
    }
  }
  
  @Override
  public Event getNextChoice () {
    return cur;
  }

  @Override
  public Class<Event> getChoiceType () {
    return Event.class;
  }

  @Override
  public boolean hasMoreChoices () {
    if (cur == null){
      return (nProcessed == 0);
    } else {
      return (cur.getAlt() != null);
    }
  }

  @Override
  public void advance () {
    if (cur == null){
      if (nProcessed == 0){
        cur = base;
        nProcessed = 1;
      }
    } else {
      cur = cur.getAlt();
      nProcessed++;
    }
  }

  @Override
  public void reset () {
    isDone = false;
    cur = null;
    nProcessed = 0;
  }

  @Override
  public int getTotalNumberOfChoices () {
    return base.getNumberOfAlternatives();
  }

  @Override
  public int getProcessedNumberOfChoices () {
    return nProcessed;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getName());
    sb.append("[id=\"");
    sb.append(id);
    sb.append('"');

    sb.append(",isCascaded:");
    sb.append(Boolean.toString(isCascaded));

    sb.append(",{");
    for (Event e=base; e!= null; e = e.getAlt()){
      if (e != base){
        sb.append(',');
      }
      if (e == cur){
        sb.append(MARKER);        
      }
      sb.append(e.toString());
    }
    sb.append("}]");
    
    return sb.toString();
  }
}
