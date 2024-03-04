package unsw.blackout;

import java.util.List;

import unsw.response.models.EntityInfoResponse;

public interface Entity {
    EntityInfoResponse getInfo();

    List<String> inRange();
}
