package me.chanjar.pvp.equipment.model;

import me.chanjar.pvp.equipment.repo.EquipmentRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquipmentModel implements Equipment {

  private String id;

  private EquipmentType type;

  private int price;

  private int sellPrice;

  private Attribute attribute = new Attribute();

  private List<String> dependsOn = new ArrayList<>(3);

  private String remark;

  private transient EquipmentRepository equipmentRepository;

  @Override
  public List<String> getDependsOnRecursively() {

    List<String> result = new ArrayList<>();
    for (String id : dependsOn) {
      result.addAll(equipmentRepository.getById(id).getDependsOnRecursively());
      result.add(id);
    }
    return result;
  }

  @Override
  public Permutation calculatePermutation(int maxPreInsertOffset) {

    if (CollectionUtils.isEmpty(dependsOn)) {

      return new Permutation(
          Collections.singletonList(
              new Sequence(Collections.singletonList(id))
          )
      );
    }

    List<Permutation> subPermutations = new ArrayList<>();
    for (String id : dependsOn) {
      subPermutations.add(equipmentRepository.getById(id).calculatePermutation(maxPreInsertOffset));
    }

    Permutation result = subPermutations.get(0);

    for (int j = 1; j < subPermutations.size(); j++) {
      Permutation sub2 = subPermutations.get(j);
      result = result.merge(sub2, maxPreInsertOffset);

    }

    result.getSequenceList().stream().forEach(s -> s.append(this.id));
    return result;

  }


  @Override
  public int[] getOccupancyDeltaList() {
    if (CollectionUtils.isEmpty(dependsOn)) {
      return new int[] { 1 };
    }

    int[] res = new int[0];
    for (String id : dependsOn) {
      res = ArrayUtils.addAll(
          res,
          equipmentRepository.getById(id).getOccupancyDeltaList()
      );
    }

    return ArrayUtils.add(res, 1 - dependsOn.size());
  }

  @Override
  public EquipmentType getType() {
    return type;
  }

  @Override
  public void setType(EquipmentType type) {
    this.type = type;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public int getPrice() {
    return price;
  }

  @Override
  public void setPrice(int price) {
    this.price = price;
  }

  @Override
  public List<String> getDependsOn() {
    return dependsOn;
  }

  @Override
  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }

  @Override
  public int getSellPrice() {
    return sellPrice;
  }

  @Override
  public void setSellPrice(int sellPrice) {
    this.sellPrice = sellPrice;
  }

  @Override
  public String getRemark() {
    return remark;
  }

  @Override
  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public Attribute getAttribute() {
    return attribute;
  }

  @Override
  public void setAttribute(Attribute attribute) {
    this.attribute = attribute;
  }

  public void setEquipmentRepository(EquipmentRepository equipmentRepository) {
    this.equipmentRepository = equipmentRepository;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("id", id)
        .append("type", type)
        .append("price", price)
        .append("sellPrice", sellPrice)
        .append("attribute", attribute)
        .append("dependsOn", dependsOn)
        .append("remark", remark)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EquipmentModel that = (EquipmentModel) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}