module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "or_podanie_issue_documents",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      or_podanie_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "or_podanie_issues",
          key: "id",
        },
      },
      name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      delivery_date: {
        type: DataTypes.DATEONLY,
        allowNull: false,
      },
      ruz_deposit_date: {
        type: DataTypes.DATEONLY,
        allowNull: true,
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
      tableName: "or_podanie_issue_documents",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.or_podanie_issue_documents_on_or_podanie_issue_id",
          fields: [{ name: "or_podanie_issue_id" }],
        },
        {
          name: "or_podanie_issue_documents_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
