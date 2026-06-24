import * as cdk from 'aws-cdk-lib/core';
import * as budgets from 'aws-cdk-lib/aws-budgets';
import { Construct } from 'constructs';

export interface MonitoringStackProps extends cdk.StackProps {
  alertEmail: string;
  /** Monthly USD threshold that triggers the billing alert (default: 10) */
  monthlyBudgetUsd?: number;
}

/**
 * Account-level cost budget — AWS Budgets is not region-specific so this stack
 * can be deployed to any already-bootstrapped region (us-east-2).
 */
export class MonitoringStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: MonitoringStackProps) {
    super(scope, id, props);

    const amount = props.monthlyBudgetUsd ?? 10;

    new budgets.CfnBudget(this, 'MonthlyBudget', {
      budget: {
        budgetName: 'TowerAnalyzer-Monthly',
        budgetType: 'COST',
        timeUnit: 'MONTHLY',
        budgetLimit: { amount, unit: 'USD' },
      },
      notificationsWithSubscribers: [{
        notification: {
          notificationType: 'ACTUAL',
          comparisonOperator: 'GREATER_THAN',
          threshold: 100,
          thresholdType: 'PERCENTAGE',
        },
        subscribers: [{
          subscriptionType: 'EMAIL',
          address: props.alertEmail,
        }],
      }],
    });
  }
}