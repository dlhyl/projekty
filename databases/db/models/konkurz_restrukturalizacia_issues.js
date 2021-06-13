module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "konkurz_restrukturalizacia_issues",
    {
      id: {
        autoIncrement: true,
        type: DataTypes.INTEGER,
        allowNull: false,
        primaryKey: true,
      },
      bulletin_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "bulletin_issues",
          key: "id",
        },
      },
      raw_issue_id: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
          model: "raw_issues",
          key: "id",
        },
      },
      court_name: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      file_reference: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      ics: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      released_by: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      releaser_position: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      sent_by: {
        type: DataTypes.STRING,
        allowNull: true,
      },
      released_date: {
        type: DataTypes.DATEONLY,
        allowNull: false,
      },
      debtor_id: {
        type: DataTypes.INTEGER,
        allowNull: true,
        references: {
          model: "konkurz_restrukturalizacia_actors",
          key: "id",
        },
      },
      kind: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      heading: {
        type: DataTypes.TEXT,
        allowNull: true,
      },
      decision: {
        type: DataTypes.TEXT,
        allowNull: true,
      },
      announcement: {
        type: DataTypes.TEXT,
        allowNull: true,
      },
      advice: {
        type: DataTypes.TEXT,
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
      tableName: "konkurz_restrukturalizacia_issues",
      schema: "ov",
      timestamps: false,
      indexes: [
        {
          name: "index_ov.konkurz_restrukturalizacia_issues_on_bulletin_issue_id",
          fields: [{ name: "bulletin_issue_id" }],
        },
        {
          name: "index_ov.konkurz_restrukturalizacia_issues_on_raw_issue_id",
          unique: true,
          fields: [{ name: "raw_issue_id" }],
        },
        {
          name: "index_ov.konkurz_restrukturalizacia_issues_on_updated_at_and_id",
          unique: true,
          fields: [{ name: "updated_at" }, { name: "id" }],
        },
        {
          name: "konkurz_restrukturalizacia_issues_pkey",
          unique: true,
          fields: [{ name: "id" }],
        },
      ],
    }
  );
};
