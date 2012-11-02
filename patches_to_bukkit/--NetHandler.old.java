package net.minecraft.server;

public abstract class NetHandler
{
  public abstract boolean c();

  public void a(Packet51MapChunk paramPacket51MapChunk)
  {
  }

  public void onUnhandledPacket(Packet paramPacket)
  {
  }

  public void a(String paramString, Object[] paramArrayOfObject)
  {
  }

  public void a(Packet255KickDisconnect paramPacket255KickDisconnect)
  {
    onUnhandledPacket(paramPacket255KickDisconnect);
  }

  public void a(Packet1Login paramPacket1Login) {
    onUnhandledPacket(paramPacket1Login);
  }

  public void a(Packet10Flying paramPacket10Flying) {
    onUnhandledPacket(paramPacket10Flying);
  }

  public void a(Packet52MultiBlockChange paramPacket52MultiBlockChange) {
    onUnhandledPacket(paramPacket52MultiBlockChange);
  }

  public void a(Packet14BlockDig paramPacket14BlockDig) {
    onUnhandledPacket(paramPacket14BlockDig);
  }

  public void a(Packet53BlockChange paramPacket53BlockChange) {
    onUnhandledPacket(paramPacket53BlockChange);
  }

  public void a(Packet50PreChunk paramPacket50PreChunk) {
    onUnhandledPacket(paramPacket50PreChunk);
  }

  public void a(Packet20NamedEntitySpawn paramPacket20NamedEntitySpawn) {
    onUnhandledPacket(paramPacket20NamedEntitySpawn);
  }

  public void a(Packet30Entity paramPacket30Entity) {
    onUnhandledPacket(paramPacket30Entity);
  }

  public void a(Packet34EntityTeleport paramPacket34EntityTeleport) {
    onUnhandledPacket(paramPacket34EntityTeleport);
  }

  public void a(Packet15Place paramPacket15Place) {
    onUnhandledPacket(paramPacket15Place);
  }

  public void a(Packet16BlockItemSwitch paramPacket16BlockItemSwitch) {
    onUnhandledPacket(paramPacket16BlockItemSwitch);
  }

  public void a(Packet29DestroyEntity paramPacket29DestroyEntity) {
    onUnhandledPacket(paramPacket29DestroyEntity);
  }

  public void a(Packet21PickupSpawn paramPacket21PickupSpawn) {
    onUnhandledPacket(paramPacket21PickupSpawn);
  }

  public void a(Packet22Collect paramPacket22Collect) {
    onUnhandledPacket(paramPacket22Collect);
  }

  public void a(Packet3Chat paramPacket3Chat) {
    onUnhandledPacket(paramPacket3Chat);
  }

  public void a(Packet23VehicleSpawn paramPacket23VehicleSpawn) {
    onUnhandledPacket(paramPacket23VehicleSpawn);
  }

  public void a(Packet18ArmAnimation paramPacket18ArmAnimation) {
    onUnhandledPacket(paramPacket18ArmAnimation);
  }

  public void a(Packet19EntityAction paramPacket19EntityAction) {
    onUnhandledPacket(paramPacket19EntityAction);
  }

  public void a(Packet2Handshake paramPacket2Handshake) {
    onUnhandledPacket(paramPacket2Handshake);
  }

  public void a(Packet24MobSpawn paramPacket24MobSpawn) {
    onUnhandledPacket(paramPacket24MobSpawn);
  }

  public void a(Packet4UpdateTime paramPacket4UpdateTime) {
    onUnhandledPacket(paramPacket4UpdateTime);
  }

  public void a(Packet6SpawnPosition paramPacket6SpawnPosition) {
    onUnhandledPacket(paramPacket6SpawnPosition);
  }

  public void a(Packet28EntityVelocity paramPacket28EntityVelocity) {
    onUnhandledPacket(paramPacket28EntityVelocity);
  }

  public void a(Packet40EntityMetadata paramPacket40EntityMetadata) {
    onUnhandledPacket(paramPacket40EntityMetadata);
  }

  public void a(Packet39AttachEntity paramPacket39AttachEntity) {
    onUnhandledPacket(paramPacket39AttachEntity);
  }

  public void a(Packet7UseEntity paramPacket7UseEntity) {
    onUnhandledPacket(paramPacket7UseEntity);
  }

  public void a(Packet38EntityStatus paramPacket38EntityStatus) {
    onUnhandledPacket(paramPacket38EntityStatus);
  }

  public void a(Packet8UpdateHealth paramPacket8UpdateHealth) {
    onUnhandledPacket(paramPacket8UpdateHealth);
  }

  public void a(Packet9Respawn paramPacket9Respawn) {
    onUnhandledPacket(paramPacket9Respawn);
  }

  public void a(Packet60Explosion paramPacket60Explosion) {
    onUnhandledPacket(paramPacket60Explosion);
  }

  public void a(Packet100OpenWindow paramPacket100OpenWindow) {
    onUnhandledPacket(paramPacket100OpenWindow);
  }

  public void handleContainerClose(Packet101CloseWindow paramPacket101CloseWindow) {
    onUnhandledPacket(paramPacket101CloseWindow);
  }

  public void a(Packet102WindowClick paramPacket102WindowClick) {
    onUnhandledPacket(paramPacket102WindowClick);
  }

  public void a(Packet103SetSlot paramPacket103SetSlot) {
    onUnhandledPacket(paramPacket103SetSlot);
  }

  public void a(Packet104WindowItems paramPacket104WindowItems) {
    onUnhandledPacket(paramPacket104WindowItems);
  }

  public void a(Packet130UpdateSign paramPacket130UpdateSign) {
    onUnhandledPacket(paramPacket130UpdateSign);
  }

  public void a(Packet105CraftProgressBar paramPacket105CraftProgressBar) {
    onUnhandledPacket(paramPacket105CraftProgressBar);
  }

  public void a(Packet5EntityEquipment paramPacket5EntityEquipment) {
    onUnhandledPacket(paramPacket5EntityEquipment);
  }

  public void a(Packet106Transaction paramPacket106Transaction) {
    onUnhandledPacket(paramPacket106Transaction);
  }

  public void a(Packet25EntityPainting paramPacket25EntityPainting) {
    onUnhandledPacket(paramPacket25EntityPainting);
  }

  public void a(Packet54PlayNoteBlock paramPacket54PlayNoteBlock) {
    onUnhandledPacket(paramPacket54PlayNoteBlock);
  }

  public void a(Packet200Statistic paramPacket200Statistic) {
    onUnhandledPacket(paramPacket200Statistic);
  }

  public void a(Packet17EntityLocationAction paramPacket17EntityLocationAction) {
    onUnhandledPacket(paramPacket17EntityLocationAction);
  }

  public void a(Packet70Bed paramPacket70Bed)
  {
    onUnhandledPacket(paramPacket70Bed);
  }

  public void a(Packet71Weather paramPacket71Weather) {
    onUnhandledPacket(paramPacket71Weather);
  }

  public void a(Packet131ItemData paramPacket131ItemData) {
    onUnhandledPacket(paramPacket131ItemData);
  }

  public void a(Packet61WorldEvent paramPacket61WorldEvent) {
    onUnhandledPacket(paramPacket61WorldEvent);
  }

  public void a(Packet254GetInfo paramPacket254GetInfo) {
    onUnhandledPacket(paramPacket254GetInfo);
  }

  public void a(Packet41MobEffect paramPacket41MobEffect) {
    onUnhandledPacket(paramPacket41MobEffect);
  }

  public void a(Packet42RemoveMobEffect paramPacket42RemoveMobEffect) {
    onUnhandledPacket(paramPacket42RemoveMobEffect);
  }

  public void a(Packet201PlayerInfo paramPacket201PlayerInfo) {
    onUnhandledPacket(paramPacket201PlayerInfo);
  }

  public void a(Packet0KeepAlive paramPacket0KeepAlive) {
    onUnhandledPacket(paramPacket0KeepAlive);
  }

  public void a(Packet43SetExperience paramPacket43SetExperience) {
    onUnhandledPacket(paramPacket43SetExperience);
  }

  public void a(Packet107SetCreativeSlot paramPacket107SetCreativeSlot) {
    onUnhandledPacket(paramPacket107SetCreativeSlot);
  }

  public void a(Packet26AddExpOrb paramPacket26AddExpOrb) {
    onUnhandledPacket(paramPacket26AddExpOrb);
  }

  public void a(Packet108ButtonClick paramPacket108ButtonClick) {
  }

  public void a(Packet250CustomPayload paramPacket250CustomPayload) {
  }

  public void a(Packet35EntityHeadRotation paramPacket35EntityHeadRotation) {
    onUnhandledPacket(paramPacket35EntityHeadRotation);
  }

  public void a(Packet132TileEntityData paramPacket132TileEntityData) {
    onUnhandledPacket(paramPacket132TileEntityData);
  }

  public void a(Packet202Abilities paramPacket202Abilities) {
    onUnhandledPacket(paramPacket202Abilities);
  }
}

