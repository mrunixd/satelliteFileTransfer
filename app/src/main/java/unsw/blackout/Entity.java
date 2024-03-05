package unsw.blackout;

import unsw.response.models.EntityInfoResponse;

public interface Entity {
    EntityInfoResponse getInfo();

    int getRange();

}
