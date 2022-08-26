package shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long requesterId);

    @Query(nativeQuery = true, value = "WITH temp AS " +
            "(SELECT id, description, requester_id, created, ROW_NUMBER() OVER (ORDER BY created) AS line_number " +
            "FROM item_requests) " +
            "SELECT * FROM temp WHERE line_number >= ? " +
            "LIMIT ?;")
    List<ItemRequest> findOthersRequests(int from, int size);

    @Query(nativeQuery = true, value = "SELECT * FROM item_requests WHERE requester_id <> ?")
    List<ItemRequest> findOthersRequestsWithoutBorders(long requesterId);
}