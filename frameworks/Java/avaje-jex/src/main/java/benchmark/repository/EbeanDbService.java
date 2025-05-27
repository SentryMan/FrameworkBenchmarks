package benchmark.repository;

import benchmark.model.Fortune;
import benchmark.model.World;
import io.ebean.Database;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class EbeanDbService implements DbService {

  Database readOnly;
  Database updateOnly;

  EbeanDbService(@Named("readOnly") Database readOnly, @Named("updateOnly") Database updateOnly) {
    this.readOnly = readOnly;
    this.updateOnly = updateOnly;
  }

  @Override
  public List<World> getWorld(int num) {

    String select = "select id, randomNumber from World where id IN (?)";

    return readOnly.findDto(World.class, select).setParameter(getRandomNumberSet(num)).findList();
  }

  @Override
  public List<Fortune> getFortune() throws SQLException {

    String select = "select id, message from Fortune";
    List<Fortune> fortuneList = new ArrayList<>(readOnly.findDto(Fortune.class, select).findList());

    fortuneList.add(new Fortune(defaultFortuneId, defaultFortuneMessage));

    fortuneList.sort(Comparator.comparing(Fortune::message));
    return fortuneList;
  }

  @Override
  public List<World> updateWorld(int num) throws SQLException {

    String update = "update World set randomNumber = ? where id = ?";
    List<World> worldList = getWorld(num);
    var sqlUpdate = updateOnly.sqlUpdate(update);

    for (World world : worldList) {
      int newRandomNumber;
      do {
        newRandomNumber = getRandomNumber();
      } while (newRandomNumber == world.getRandomNumber());

      sqlUpdate.setParameter(1, newRandomNumber).setParameter(2, world.getId()).addBatch();

      world.setRandomNumber(newRandomNumber);
    }

    sqlUpdate.executeBatch();

    return worldList;
  }
}
