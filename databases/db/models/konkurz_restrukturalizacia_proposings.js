module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "konkurz_restrukturalizacia_proposings",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "konkurz_restrukturalizacia_issues",
          key: "id",
        },
      },
      actor_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "konkurz_restrukturalizacia_actors",
          key: "id",
        },
      },
      created_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
      updated_at: {
        type: DataTypes.DATE,
        allowNull: false,
      },
    },
    {
      sequelize,
      tableName: "konkurz_restrukturalizacia_proposings",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.konkurz_restrukturalizacia_proposings_on_issue_id",
          fields: [{ name: "issue_id" }],
        },
        {
          name: "konkurz_restrukturalizacia_proposings_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
