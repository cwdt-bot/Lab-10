import java.util.Scanner;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A BusSg class encapsulate the data related to the bus services and
 * bus stops in Singapore, and supports queries to the data.
 *
 * @author: Ooi Wei Tsang
 * @version: CS2030 AY19/20 Semester 1, Lab 10
 */
class BusSg {

  /**
   * Return the set of bus services that serve this bus stop as
   * a set.  Query the web server.
   * @return A completablegfuture set of BusService that serve this bus stop.
   */
  public static CompletableFuture<Set<BusService>> getBusServices(BusStop stop) {
    CompletableFuture<String> stops = BusAPI.getBusServicesAt(stop.getStopId());
    return stops.thenApply(x -> {
      return new Scanner(x)
      .useDelimiter("\n")
      .tokens()
      .skip(1) // skip first line
      .flatMap(line -> Stream.of(line.split(",")))
      .map(id -> new BusService(id))
      .collect(Collectors.toSet());
      });
  }


  /**
   * Given a bus stop and a name, find the bus services that serve between
   * the given stop and any bus stop with matching mame.
   * @param  stop The bus stop.  Assume to be not null.
   * @param  searchString The (partial) name of other bus stops, assume not null.
   * @return The CompletableFuture bus routes between the stops.
   */
  public static CompletableFuture<BusRoutes> findBusServicesBetween(BusStop stop, String searchString) {
    return getBusServices(stop).thenApply(x -> {
      return x.stream()
      .collect(Collectors.toMap(
        service -> service, 
        service -> service.findStopsWith(searchString)));
    }).thenApply(x -> new BusRoutes(stop, searchString, x));
  }


}