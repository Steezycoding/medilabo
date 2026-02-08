export enum RiskLevel {
  NONE = 'NONE',
  BORDERLINE = 'BORDERLINE',
  IN_DANGER = 'IN_DANGER',
  EARLY_ONSET = 'EARLY_ONSET',
}

export interface RiskLevelResponse {
  'riskLevel': RiskLevel;
}
