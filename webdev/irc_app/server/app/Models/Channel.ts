import {
  BaseModel,
  beforeFetch,
  beforeFind,
  belongsTo,
  BelongsTo,
  column,
  HasMany,
  hasMany,
  ManyToMany,
  manyToMany,
  ModelQueryBuilderContract,
} from '@ioc:Adonis/Lucid/Orm';
import { DateTime } from 'luxon';
import Blacklist from './Blacklist';
import Invitation from './Invitation';
import Message from './Message';
import User from './User';

export type ChannelType = 'private' | 'public';

export default class Channel extends BaseModel {
  @column({ isPrimary: true })
  public id: number;

  @column()
  public channelname: string;

  @column()
  public ownerId: number;

  @column()
  public type: ChannelType;

  @column.dateTime({ autoCreate: true })
  public createdAt: DateTime;

  @column.dateTime({ autoCreate: true, autoUpdate: true })
  public updatedAt: DateTime;

  @belongsTo(() => User, {
    foreignKey: 'ownerId',
  })
  public owner: BelongsTo<typeof User>;

  @manyToMany(() => User, {
    pivotTable: 'channel_users',
    pivotForeignKey: 'channel_id',
    pivotRelatedForeignKey: 'user_id',
  })
  public users: ManyToMany<typeof User>;

  @hasMany(() => Message, {
    foreignKey: 'channelId',
  })
  public messages: HasMany<typeof Message>;

  @hasMany(() => Invitation, {
    foreignKey: 'channelId',
  })
  public invitations: HasMany<typeof Invitation>;

  @hasMany(() => Blacklist, {
    foreignKey: 'channelId',
  })
  public blacklist: HasMany<typeof Blacklist>;

  @hasMany(() => Message, {
    foreignKey: 'channelId',
    onQuery: (query) => query.orderBy('id', 'desc').limit(1),
  })
  public lastMessage: HasMany<typeof Message>;

  @beforeFind()
  @beforeFetch()
  public static async ignoreInactive(
    query: ModelQueryBuilderContract<typeof Channel>
  ) {
    const dateMinus30d = new Date(Date.now() - 1000 * 60 * 60 * 24 * 30);
    query.where((subquery) => {
      subquery.where('created_at', '>', dateMinus30d.toISOString());
      subquery.orWhere((subquery) =>
        subquery
          .where('created_at', '<', dateMinus30d.toISOString())
          .andWhereHas('lastMessage', (msgQuery) =>
            msgQuery.where('created_at', '>=', dateMinus30d.toISOString())
          )
      );
    });
  }

  public serializeExtras() {
    return {
      kickCount: Number(this.$extras.kickCount),
    };
  }
}
