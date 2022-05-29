import Hash from '@ioc:Adonis/Core/Hash';
import {
  BaseModel,
  beforeSave,
  column,
  HasMany,
  hasMany,
  ManyToMany,
  manyToMany,
} from '@ioc:Adonis/Lucid/Orm';
import { DateTime } from 'luxon';
import Blacklist from './Blacklist';
import Channel from './Channel';
import Invitation from './Invitation';

export default class User extends BaseModel {
  @column({ isPrimary: true })
  public id: number;

  @column()
  public firstname: string;

  @column()
  public lastname: string;

  @column()
  public username: string;

  @column()
  public image: string;

  @column()
  public email: string;

  @column({ serializeAs: null })
  public password: string;

  @column({ serializeAs: null })
  public rememberMeToken?: string;

  @column.dateTime({ autoCreate: true })
  public createdAt: DateTime;

  @column.dateTime({ autoCreate: true, autoUpdate: true })
  public updatedAt: DateTime;

  @beforeSave()
  public static async hashPassword(user: User) {
    if (user.$dirty.password) {
      user.password = await Hash.make(user.password);
    }
  }

  @hasMany(() => Invitation, {
    foreignKey: 'userId',
  })
  public invitations: HasMany<typeof Invitation>;

  @manyToMany(() => Channel, {
    pivotTable: 'channel_users',
    pivotForeignKey: 'user_id',
    pivotRelatedForeignKey: 'channel_id',
  })
  public channels: ManyToMany<typeof Channel>;

  @manyToMany(() => Channel, {
    pivotTable: 'channel_users',
    pivotForeignKey: 'user_id',
    pivotRelatedForeignKey: 'channel_id',
    onQuery: (query) => {
      query.where('type', 'private');
    },
  })
  public privateChannels: ManyToMany<typeof Channel>;

  @manyToMany(() => Channel, {
    pivotTable: 'channel_users',
    pivotForeignKey: 'user_id',
    pivotRelatedForeignKey: 'channel_id',
    onQuery: (query) => {
      query.where('type', 'public');
    },
  })
  public publicChannels: ManyToMany<typeof Channel>;

  @hasMany(() => Blacklist, {
    foreignKey: 'blacklistedId',
  })
  public blacklist: HasMany<typeof Blacklist>;
}
